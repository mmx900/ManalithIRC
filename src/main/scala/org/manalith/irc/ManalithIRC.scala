package org.manalith.irc;

import org.apache.commons.configuration.ConfigurationException
import org.apache.commons.configuration.XMLConfiguration
import org.apache.log4j.Logger
import org.manalith.irc.ui.ApplicationWindowLoader
import org.manalith.irc.helper.LogHelper

/**
 * ManalithIRC Launcher
 *
 * @author setzer
 *
 */
object ManalithIRC extends LogHelper{
	var application: Application = null;

	/**
	 * @param args
	 */
	def main(args: Array[String]) {
		try {
			val config = new XMLConfiguration("config.xml");
			application = new Application(config);
			ApplicationWindowLoader.load;
		} catch {
			case e: ConfigurationException =>
				logger.error(e);
		}
	}
}
