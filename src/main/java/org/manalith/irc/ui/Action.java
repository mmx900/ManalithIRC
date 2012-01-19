package org.manalith.irc.ui;

import org.eclipse.swt.widgets.Widget;

public class Action {
	private String command;
	private Widget source;
	private Widget sourceContainer;

	public Action(String command, Widget source, Widget sourceContainer) {
		this(command, source);
		this.sourceContainer = sourceContainer;
	}

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

	public Widget getSourceContainer() {
		return sourceContainer;
	}
}
