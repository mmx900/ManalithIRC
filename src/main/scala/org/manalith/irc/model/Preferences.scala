package org.manalith.irc.model

import java.util.ArrayList

import scala.collection.JavaConversions.asScalaBuffer
import scala.collection.mutable.HashSet

import org.apache.commons.configuration.HierarchicalConfiguration
import org.manalith.irc.helper.LogHelper

object Preferences extends LogHelper {
	var config: HierarchicalConfiguration = null;

	private var _servers: HashSet[Server] = null;
	def servers = {
		if (_servers == null) {
			_servers = new HashSet[Server]();
			val servers = config.configurationsAt("servers.server");

			for (s <- servers) {
				val channels = new ArrayList[String]();
				for (c <- s.configurationsAt("channels.channel")) {
					channels += c.getString("name");
					//channels += c.getString("password");
				}

				val server = new Server(s.getString("host"),
					s.getInt("port"),
					s.getString("encoding"),
					s.getBoolean("verbose"), null, channels,
					s.getString("nickname"),
					s.getString("login"));

				_servers += server;
			}
		}

		_servers;
	}

	lazy val messages_disconnect = config.getString("common.messages.disconnect");
}