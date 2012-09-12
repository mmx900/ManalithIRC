package org.manalith.irc.ui;

import java.util.ArrayList
import java.util.List
import scala.collection.JavaConversions.asScalaBuffer
import org.eclipse.e4.xwt.IConstants
import org.eclipse.e4.xwt.XWT
import org.eclipse.e4.xwt.annotation.UI
import org.eclipse.swt.SWT
import org.eclipse.swt.custom.CTabFolder
import org.eclipse.swt.custom.CTabItem
import org.eclipse.swt.widgets.Event
import org.eclipse.swt.widgets.Shell
import org.manalith.irc.ManalithIRC
import org.manalith.irc.model.Connection
import org.manalith.irc.model.Server
import org.pircbotx.Channel
import org.eclipse.e4.xwt.jface.XWTDialog
import org.eclipse.jface.dialogs.InputDialog
import org.apache.commons.lang3.StringUtils
import scala.collection.mutable.Publisher
import scala.collection.mutable.Subscriber
import org.eclipse.swt.widgets.Display
import org.pircbotx.hooks.events.JoinEvent
import org.pircbotx.hooks.events.UserListEvent
import org.pircbotx.hooks.ListenerAdapter
import org.pircbotx.hooks.events.TopicEvent
import org.pircbotx.PircBotX
import org.manalith.irc.model.ConnectionManager

class ApplicationWindow extends Publisher[Action] {
	private val EVENT_WINDOW_DISPOSED = "WindowDisposed";
	private val EVENT_CONNECT_BUTTON_CLICKED = "ConnectButtonClicked";

	val channelViewList: List[ChannelView] = new ArrayList[ChannelView]();

	subscribe(new ApplicationWindowActionListener());

	@UI
	private var shell: Shell = null;

	@UI
	private var tabFolder: CTabFolder = null;

	def getChannelView(channelName: String): ChannelView = {
		for (view <- channelViewList) {
			if (view.channelName == channelName) {
				return view;
			}
		}

		return null;
	}

	/**
	 * Open the window.
	 */

	def createServerTab(title: String, connection: Connection): CTabItem = {
		val item = new CTabItem(tabFolder, SWT.CLOSE);
		item.setText(title);
		val view = new ServerMessageView(tabFolder, SWT.NONE,
			connection);
		item.setControl(view);
		return item;
	}

	def createChannelTab(channel: Channel, connection: Connection): CTabItem = {
		val item = new CTabItem(tabFolder, SWT.CLOSE);
		item.setText(channel.getName());
		val view = new ChannelView(tabFolder, SWT.NONE, channel,
			connection);
		item.setControl(view);
		tabFolder.setSelection(item);
		channelViewList.add(view);
		return item;
	}

	def closeTab(tab: IrcTab) {

	}

	def onWindowDispose(event: Event) {
		publish(new Action(EVENT_WINDOW_DISPOSED, event.widget));
	}

	def onConnectButtonClick(event: Event) {
		publish(new Action(EVENT_CONNECT_BUTTON_CLICKED, event.widget));
	}

	def onJoinButtonClick(event: Event) {
		//		val dlg = new InputDialog(shell, null, null, null, null);
		//		dlg.open();
		//		val channelName = dlg.getValue();
		//		if (StringUtils.isNotBlank(channelName)) {
		//			createChannelTab(channel, connection)
		//		}

		//val dialog = new XWTDialog(shell, null, null, classOf[JoinDialog]);
		//dialog.open();
	}

	def onPropertiesButtonClick(event: Event) {
		new PropertyShell(event.widget.getDisplay()).open();
	}

	private class ApplicationWindowActionListener extends Subscriber[Action, Publisher[Action]] {
		def notify(pub: Publisher[Action], action: Action) = {
			action.command match {
				case EVENT_CONNECT_BUTTON_CLICKED => {
					// XXX
					val config = ManalithIRC.config;
					val channelsConfig = config
						.getList("server.defaultChannels");
					val channels = new ArrayList[String]();
					for (c <- channelsConfig) {
						channels.add(c.toString());
					}
					val server = new Server(config.getString("server.host"),
						config.getInt("server.port"),
						config.getString("server.encoding"),
						config.getBoolean("server.verbose"), null, channels,
						config.getString("server.nickname"));
					val connection = ConnectionManager.getConnection(server);
					connection.addEventListener(new ConnectionEventDispatcher(connection));

					val serverTab = createServerTab(server.hostname,
						connection);
					val view = serverTab
						.getControl().asInstanceOf[ServerMessageView];
					view.subscribe(this);

					if (connection.connect) {

					}
				}
				case EVENT_WINDOW_DISPOSED => {
					//TODO ManalithIRC 클래스에서 선언하도록 이동
					ManalithIRC.onExit;
				}
			}
		}
	}

	private class ConnectionEventDispatcher(connection: Connection) extends ListenerAdapter[PircBotX] {

		@throws(classOf[Exception])
		override def onTopic(event: TopicEvent[PircBotX]) = {
			val view = getChannelView(event.getChannel()
				.getName());
			if (view != null) {
				Display.getDefault().asyncExec(new Runnable() {
					def run = {
						view.setTopic(event.getTopic());
					}
				});
			}
		}

		@throws(classOf[Exception])
		override def onUserList(event: UserListEvent[PircBotX]) = {
			val view = getChannelView(event.getChannel()
				.getName());
			if (view != null) {
				Display.getDefault().asyncExec(new Runnable() {
					def run = {
						view.updateUserList(event.getUsers());
					}
				});
			}
		}

		@throws(classOf[Exception])
		override def onJoin(event: JoinEvent[PircBotX]) = {
			val nick = event.getUser().getNick();

			if (nick.equals(nick)) {
				val view = getChannelView(event
					.getChannel().getName());

				if (view == null) {
					Display.getDefault().asyncExec(new Runnable() {
						def run = {
							createChannelTab(event.getChannel(),
								connection);
						}
					});
				}
			}
		}
	}
}

object ApplicationWindowLoader {
	def load = {
		val url = classOf[ApplicationWindow].getResource(classOf[ApplicationWindow]
			.getSimpleName() + IConstants.XWT_EXTENSION_SUFFIX);

		try {
			val control = XWT.load(url);

			val shell = control.getShell();
			shell.layout();
			centerInDisplay(shell);

			// run events loop
			// shell.pack();
			shell.open();
			while (!shell.isDisposed()) {
				if (!shell.getDisplay().readAndDispatch())
					shell.getDisplay().sleep();
			}
		} catch {
			case e: Exception =>
				e.printStackTrace();
		}
	}

	def centerInDisplay(shell: Shell) {
		val displayArea = shell.getDisplay().getClientArea();
		shell.setBounds(displayArea.width / 4, displayArea.height / 4,
			displayArea.width / 2, displayArea.height / 2);
	}
}