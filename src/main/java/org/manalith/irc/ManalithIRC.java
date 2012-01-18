package org.manalith.irc;

import org.apache.commons.configuration.XMLConfiguration;
import org.manalith.irc.ui.ApplicationWindow;
import org.manalith.irc.ui.ApplicationWindowListener;

public class ManalithIRC {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			XMLConfiguration config = new XMLConfiguration("config.xml");
			// config.setListDelimiter(',');
			// DefaultConfigurationBuilder builder = new
			// DefaultConfigurationBuilder(
			// "config.xml");
			// Configuration config = builder.getConfiguration();
			Application application = new Application(config);
			ApplicationWindow window = new ApplicationWindow();
			window.addActionListener(new ApplicationWindowListener(window,
					application));
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
