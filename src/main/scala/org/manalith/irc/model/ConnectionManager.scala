package org.manalith.irc.model

import scala.collection.mutable.HashSet

import org.manalith.irc.helper.LogHelper
import org.pircbotx.MultiBotManager

object ConnectionManager extends LogHelper {
	lazy val manager = new MultiBotManager("ManalithIRC");
	lazy val connections = new HashSet[Connection]();

	def getConnection(server: Server): Connection = {
		connections.find(c => c.server == server) match {
			case Some(c) => return c;
			case None => {
				val connection = new Connection(server, manager.createBot(server.hostname, server.port));
				connections.add(connection);

				return connection;
			}
		}
	}

	def disconnectAllConnection(message: String) = {
		connections.foreach(c => {
			c.quitServer(message);
		});
	}
}