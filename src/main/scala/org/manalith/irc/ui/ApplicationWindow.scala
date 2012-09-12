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

class ApplicationWindow extends Publisher[Action] {
	private val EVENT_WINDOW_DISPOSED = "WindowDisposed";
	private val EVENT_CONNECT_BUTTON_CLICKED = "ConnectButtonClicked";

	private lazy val application = ManalithIRC.application;
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
	}

	def onPropertiesButtonClick(event: Event) {
		new PropertyShell(event.widget.getDisplay()).open();
	}

	private class ApplicationWindowActionListener extends Subscriber[Action, Publisher[Action]] {
		def notify(pub: Publisher[Action], action: Action) = {
			action.command match {
				case EVENT_CONNECT_BUTTON_CLICKED => {
					// XXX
					val config = application.configuration;
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
					val connection = application.createConnection(server,
						ApplicationWindow.this);

					val serverTab = createServerTab(server.hostname,
						connection);
					val view = serverTab
						.getControl().asInstanceOf[ServerMessageView];
					view.subscribe(this);

					if (connection.connect) {

					}
				}
				case EVENT_WINDOW_DISPOSED => {
					application.disconnectAllConnection;
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