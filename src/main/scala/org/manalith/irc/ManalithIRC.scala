package org.manalith.irc;

import org.apache.commons.configuration.ConfigurationException
import org.apache.commons.configuration.XMLConfiguration
import org.manalith.irc.helper.LogHelper
import org.manalith.irc.model.ConnectionManager
import org.manalith.irc.model.Preferences
import org.manalith.irc.ui.ApplicationWindowLoader

/**
 * ManalithIRC Launcher
 *
 * @author setzer
 *
 */
object ManalithIRC extends LogHelper {
	/**
	 * @param args
	 */
	def main(args: Array[String]) {
		try {
			Preferences.config = new XMLConfiguration("config.xml");
			ApplicationWindowLoader.load;
		} catch {
			case e: ConfigurationException =>
				logger.error(e);
		}
	}

	def onExit() {
		ConnectionManager.disconnectAllConnection(Preferences.messages_disconnect);
	}
}
