package org.manalith.irc.ui;

import org.eclipse.swt.widgets.Widget

class Action(var command: String, var source: Widget, var sourceContainer: Widget) {

	def this(command: String, source: Widget) = this(command, source, null);

	assert(command != null && source != null);
}