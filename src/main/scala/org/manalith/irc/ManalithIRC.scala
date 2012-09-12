package org.manalith.irc;

import org.apache.commons.configuration.ConfigurationException
import org.apache.commons.configuration.XMLConfiguration
import org.apache.log4j.Logger
import org.manalith.irc.ui.ApplicationWindowLoader
import org.manalith.irc.helper.LogHelper
import org.manalith.irc.model.ConnectionManager
import org.apache.commons.configuration.Configuration

/**
 * ManalithIRC Launcher
 *
 * @author setzer
 *
 */
object ManalithIRC extends LogHelper {
	var config: Configuration = null;

	/**
	 * @param args
	 */
	def main(args: Array[String]) {
		try {
			config = new XMLConfiguration("config.xml");
			ApplicationWindowLoader.load;
		} catch {
			case e: ConfigurationException =>
				logger.error(e);
		}
	}

	def onExit() {
		ConnectionManager.disconnectAllConnection(config
			.getString("common.messages.disconnect"));
	}
}
