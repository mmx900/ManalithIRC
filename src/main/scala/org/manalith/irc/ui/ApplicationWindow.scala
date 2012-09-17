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
import org.manalith.irc.helper.SwtUtil
import org.eclipse.swt.events.DisposeListener
import org.eclipse.swt.events.DisposeEvent
import org.eclipse.swt.custom.CTabFolder2Adapter
import org.eclipse.swt.custom.CTabFolderEvent
import org.eclipse.jface.dialogs.MessageDialog
import org.manalith.irc.helper.LogHelper

class ApplicationWindow extends Publisher[Action] with LogHelper {
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
			if (view.channel.getName() == channelName) {
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

		tabFolder.addCTabFolder2Listener(new CTabFolder2Adapter() {
			override def close(event: CTabFolderEvent) = {
				if (event.item.equals(item)) {
					event.doit = MessageDialog.openConfirm(shell, "확인",
						"이 서버의 모든 채널 탭도 함께 닫습니다. 계속 하시겠습니까?");
				}
			}
		});

		item.addDisposeListener(new DisposeListener() {
			def widgetDisposed(event: DisposeEvent) = {
				//quit 메시지를 보내기 위해 관련 탭을 닫기 전 먼저 연결을 끊는다.
				connection.disconnect;

				val toClose = new ArrayList[ChannelView]()

				for (view <- channelViewList) {
					if (view.connection == connection) {
						//java.util.ConcurrentModificationException 방지
						toClose.add(view);
					}
				}

				for (view <- toClose) {
					//view를 포함하는 CTabItem을 dispose
					view.getParent().dispose();
				}
			}
		});

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

		item.addDisposeListener(new DisposeListener() {
			def widgetDisposed(event: DisposeEvent) = {
				connection.partChannel(channel);
				channelViewList.remove(view);
			}
		});

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
		//				val dlg = new InputDialog(shell, null, null, null, null);
		//		dlg.open();
		//		val channelName = dlg.getValue();
		//		if (StringUtils.isNotBlank(channelName)) {
		//			createChannelTab(channel, connection)
		//		}

		//		val dialog = new XWTDialog(shell, null, null, classOf[JoinDialog]);
		//		dialog.open();
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

					val servers = config.configurationsAt("servers.server");

					for (s <- servers) {
						val channels = new ArrayList[String]();
						for (c <- s.configurationsAt("channels.channel")) {
							channels.add(c.getString("name"));
							//channels.add(c.getString("password"));
						}

						val server = new Server(s.getString("host"),
							s.getInt("port"),
							s.getString("encoding"),
							s.getBoolean("verbose"), null, channels,
							s.getString("nickname"),
							s.getString("login"));
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
		override def onUserList(event: UserListEvent[PircBotX]) = {
			val view = getChannelView(event.getChannel()
				.getName());
			if (view != null) {
				SwtUtil.asyncExec(view.updateUserList());
			}
		}

		@throws(classOf[Exception])
		override def onJoin(event: JoinEvent[PircBotX]) = {
			val nick = event.getUser().getNick();

			if (nick.equals(nick)) {
				val view = getChannelView(event
					.getChannel().getName());

				if (view == null) {
					SwtUtil.asyncExec(createChannelTab(event.getChannel(),
						connection));
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
			SwtUtil.centerInDisplay(shell);

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
}