package org.manalith.irc;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.manalith.irc.model.Connection;
import org.manalith.irc.model.Server;

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

	public Connection createConnection(Server server) {
		Connection connection = new Connection(server);
		connectionPool.add(connection);
		return connection;
	}

	public void disconnectAllConnection() {
		for (Connection conn : connectionPool) {
			conn.quitServer(configuration.getString("common.messages.disconnect"));
		}
	}
}
