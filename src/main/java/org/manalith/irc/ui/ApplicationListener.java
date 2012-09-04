package org.manalith.irc.ui;

import org.manalith.irc.Application;

@Deprecated
public class ApplicationListener implements ActionListener {
	private ApplicationWindow window;
	private Application application;
	private ApplicationListener instance;

	public ApplicationListener(ApplicationWindow window, Application application) {
		this.window = window;
		this.application = application;
		this.instance = this;
	}

	public void onAction(Action action) {
	}
}
