package org.manalith.irc.ui;

import org.eclipse.swt.widgets.Widget;

public class Action {
	private String command;
	private Widget source;

	public Action(String command, Widget source) {
		assert command != null && source != null;

		this.command = command;
		this.source = source;
	}

	public String getCommand() {
		return command;
	}

	public Widget getSource() {
		return source;
	}
}
