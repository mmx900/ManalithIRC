package org.manalith.irc;

import java.util.ArrayList
import java.util.List

import scala.collection.JavaConversions.asScalaBuffer

import org.apache.commons.configuration.Configuration
import org.manalith.irc.model.Connection
import org.manalith.irc.model.Server
import org.manalith.irc.ui.ApplicationWindow

class Application(val configuration: Configuration) {
	private val connectionPool: List[Connection] = new ArrayList[Connection]();

	def createConnection(server: Server, window: ApplicationWindow) = {
		val connection = new Connection(server, window);
		connectionPool.add(connection);
		connection;
	}

	def disconnectAllConnection = {
		for (conn <- connectionPool) {
			conn.quitServer(configuration
				.getString("common.messages.disconnect"));
			conn.disconnect;
		}
	}
}
