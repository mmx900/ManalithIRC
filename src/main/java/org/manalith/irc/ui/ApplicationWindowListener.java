package org.manalith.irc.ui;

import org.apache.commons.configuration.Configuration;
import org.eclipse.swt.widgets.TabItem;
import org.manalith.irc.Application;
import org.manalith.irc.ServerListener;
import org.manalith.irc.model.Connection;
import org.manalith.irc.model.Server;

public class ApplicationWindowListener implements ActionListener {
	private ApplicationWindow window;
	private Application application;

	public ApplicationWindowListener(ApplicationWindow window,
			Application application) {
		this.window = window;
		this.application = application;
	}

	public void onAction(Action action) {
		if (action.getCommand().equals(
				ApplicationWindow.EVENT_CONNECT_BUTTON_CLICKED)) {
			Configuration config = application.getConfiguration();
			Server server = new Server();
			server.setHostname(config.getString("server.host"));
			server.setPort(config.getInt("server.port"));
			server.setEncoding(config.getString("server.encoding"));
			server.setVerbose(config.getBoolean("server.verbose"));
			server.setNickname(config.getString("server.nickname"));
			server.setDefaultChannels(config.getList("server.defaultChannels"));

			TabItem tbtmServer = window.createServerTab(server.getHostname());
			ServerMessageView composite = (ServerMessageView) tbtmServer
					.getControl();

			Connection connection = application.createConnection(server);
			connection.addEventListener(new ServerListener(connection, window,
					composite));

			if (connection.connect()) {

			}
		} else if (action.getCommand().equals(
				ApplicationWindow.EVENT_WINDOW_DISPOSED)) {
			application.disconnectAllConnection();
		}
	}

}
