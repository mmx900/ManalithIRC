package org.manalith.irc.model

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
			
			config.configurationsAt("servers.server").foreach(s => {
				val channels = s.configurationsAt("channels.channel").map(c => new Channel(c.getString("name"), c.getString("password")))

				_servers += new Server(s.getString("host"),
					s.getInt("port"),
					s.getString("encoding"),
					s.getBoolean("verbose"), null, channels,
					s.getString("nickname"),
					s.getString("login"))
			});

		}

		_servers;
	}

	lazy val messages_disconnect = config.getString("common.messages.disconnect");
}