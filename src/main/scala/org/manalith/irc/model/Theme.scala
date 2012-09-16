package org.manalith.irc.model

import org.eclipse.swt.graphics.Color
import org.eclipse.swt.widgets.Display
import org.eclipse.swt.graphics.Device

class Theme {
	var messageForeground: Color = null;
	var messageBackground: Color = null;
	var selectionForeground: Color = null;
	var selectionBackground: Color = null;
	var newData: Color = null;
	var newMessage: Color = null;
}

object Theme {
	private val theme = new Theme();
	private val device = Display.getCurrent();
	theme.messageForeground = new Color(device, 0, 0, 0);
	theme.messageBackground = new Color(device, 240, 240, 240);
	theme.selectionForeground = new Color(device, 255, 255, 255);
	theme.selectionBackground = new Color(device, 53, 110, 193);
	theme.newData = new Color(device, 153, 0, 0);
	theme.newMessage = new Color(device, 255, 0, 0);
	val current = theme;
}