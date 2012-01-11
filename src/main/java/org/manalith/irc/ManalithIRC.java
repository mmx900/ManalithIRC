package org.manalith.irc;

import org.manalith.irc.ui.ApplicationWindow;

public class ManalithIRC {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			Application application = new Application();
			ApplicationWindow window = new ApplicationWindow(application);
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
