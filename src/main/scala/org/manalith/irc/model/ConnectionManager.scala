package org.manalith.irc.model

import java.util.ArrayList

import scala.collection.JavaConversions.asScalaBuffer

import org.manalith.irc.helper.LogHelper
import org.pircbotx.MultiBotManager
import org.pircbotx.PircBotX

object ConnectionManager extends LogHelper {
	lazy val manager = new MultiBotManager("ManalithIRC");
	lazy val connections = new ArrayList[Connection]();

	def getConnection(server: Server): Connection = {
		for (connection <- connections) {
			if (connection.server == server)
				return connection;
		}

		val connection = new Connection(server, manager.createBot(server.hostname, server.port));
		connections.add(connection);

		return connection;
	}

	def disconnectAllConnection(message: String) = {
		for (conn <- connections) {
			conn.quitServer(message);
			conn.disconnect;
		}
	}
}