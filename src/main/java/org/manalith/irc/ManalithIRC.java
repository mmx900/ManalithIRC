package org.manalith.irc;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.log4j.Logger;
import org.manalith.irc.ui.ApplicationWindow;

/**
 * ManalithIRC Launcher
 * 
 * @author setzer
 * 
 */
public class ManalithIRC {
	private static final Logger logger = Logger.getLogger(ManalithIRC.class);
	public static Application application;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			XMLConfiguration config = new XMLConfiguration("config.xml");
			application = new Application(config);
			ApplicationWindow.load();
		} catch (ConfigurationException e) {
			logger.error(e);
		}
	}
}
