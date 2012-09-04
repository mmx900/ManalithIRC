package org.manalith.irc;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.manalith.irc.model.Connection;
import org.manalith.irc.model.Server;
import org.manalith.irc.ui.ApplicationWindow;

public class Application {
	private Configuration configuration;
	private List<Connection> connectionPool = new ArrayList<Connection>();

	public Configuration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}

	public Application(Configuration configuration) {
		this.configuration = configuration;
	}

	public Connection createConnection(Server server, ApplicationWindow window) {
		Connection connection = new Connection(server, window);
		connectionPool.add(connection);
		return connection;
	}

	public void disconnectAllConnection() {
		for (Connection conn : connectionPool) {
			conn.quitServer(configuration
					.getString("common.messages.disconnect"));
			conn.disconnect();
		}
	}
}
