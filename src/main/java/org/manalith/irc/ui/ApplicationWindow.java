package org.manalith.irc.ui;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.eclipse.e4.xwt.IConstants;
import org.eclipse.e4.xwt.XWT;
import org.eclipse.e4.xwt.annotation.UI;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.manalith.irc.Application;
import org.manalith.irc.ManalithIRC;
import org.manalith.irc.model.Connection;
import org.manalith.irc.model.Server;
import org.pircbotx.Channel;

public class ApplicationWindow {
	public static final String EVENT_WINDOW_DISPOSED = "WindowDisposed";

	public static final String EVENT_CONNECT_BUTTON_CLICKED = "ConnectButtonClicked";

	private Application application;
	private List<ActionListener> actionListeners = new ArrayList<ActionListener>();
	private List<ChannelView> channelViewList = new ArrayList<ChannelView>();

	@UI
	private CTabFolder tabFolder;

	public ApplicationWindow() {
		this.application = ManalithIRC.application();
		addActionListener(new ApplicationWindowActionListener());
	}

	public List<ChannelView> getChannelViewList() {
		return channelViewList;
	}

	public void setChannelViewList(List<ChannelView> channelViewList) {
		this.channelViewList = channelViewList;
	}

	public ChannelView getChannelView(String channelName) {
		for (ChannelView view : channelViewList) {
			if (view.getChannelName().equals(channelName)) {
				return view;
			}
		}

		return null;
	}

	/**
	 * Open the window.
	 */
	public static void load() {
		URL url = ApplicationWindow.class.getResource(ApplicationWindow.class
				.getSimpleName() + IConstants.XWT_EXTENSION_SUFFIX);
		try {
			Control control = XWT.load(url);

			final Shell shell = control.getShell();
			shell.layout();
			centerInDisplay(shell);

			// run events loop
			// shell.pack();
			shell.open();
			while (!shell.isDisposed()) {
				if (!shell.getDisplay().readAndDispatch())
					shell.getDisplay().sleep();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public CTabItem createServerTab(String title, Connection connection) {
		CTabItem item = new CTabItem(tabFolder, SWT.CLOSE);
		item.setText(title);
		ServerMessageView view = new ServerMessageView(tabFolder, SWT.NONE,
				connection);
		item.setControl(view);
		return item;
	}

	public CTabItem createChannelTab(Channel channel, Connection connection) {
		CTabItem item = new CTabItem(tabFolder, SWT.CLOSE);
		item.setText(channel.getName());
		ChannelView view = new ChannelView(tabFolder, SWT.NONE, channel,
				connection);
		item.setControl(view);
		tabFolder.setSelection(item);
		getChannelViewList().add(view);
		return item;
	}

	public void closeTab(IrcTab tab) {

	}

	public void addActionListener(ActionListener listener) {
		actionListeners.add(listener);
	}

	public void removeActionListener(ActionListener listener) {
		actionListeners.remove(listener);
	}

	private void onAction(Action action) {
		for (ActionListener listener : actionListeners) {
			listener.onAction(action);
		}
	}

	private static void centerInDisplay(Shell shell) {
		Rectangle displayArea = shell.getDisplay().getClientArea();
		shell.setBounds(displayArea.width / 4, displayArea.height / 4,
				displayArea.width / 2, displayArea.height / 2);
	}

	public void onWindowDispose(Event event) {
		onAction(new Action(EVENT_WINDOW_DISPOSED, event.widget));
	}

	public void onConnectButtonClick(Event event) {
		onAction(new Action(EVENT_CONNECT_BUTTON_CLICKED, event.widget));
	}

	public void onJoinButtonClick(Event event) {
	}

	public void onPropertiesButtonClick(Event event) {
		new PropertyShell(event.widget.getDisplay()).open();
	}

	private class ApplicationWindowActionListener implements ActionListener {
		public void onAction(Action action) {
			switch (action.getCommand()) {
			case ApplicationWindow.EVENT_CONNECT_BUTTON_CLICKED: {
				// XXX
				Configuration config = application.getConfiguration();
				Server server = new Server();
				server.setHostname(config.getString("server.host"));
				server.setPort(config.getInt("server.port"));
				server.setEncoding(config.getString("server.encoding"));
				server.setVerbose(config.getBoolean("server.verbose"));
				server.setNickname(config.getString("server.nickname"));
				List<Object> channelsConfig = config
						.getList("server.defaultChannels");
				List<String> channels = new ArrayList<String>();
				for (Object c : channelsConfig) {
					channels.add(c.toString());
				}
				server.setDefaultChannels(channels);
				Connection connection = application.createConnection(server,
						ApplicationWindow.this);

				CTabItem serverTab = createServerTab(server.getHostname(),
						connection);
				ServerMessageView view = (ServerMessageView) serverTab
						.getControl();
				view.addActionListener(this);

				if (connection.connect()) {

				}
				break;
			}
			case ApplicationWindow.EVENT_WINDOW_DISPOSED: {
				application.disconnectAllConnection();
				break;
			}
			}
		}
	}
}
