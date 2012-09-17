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
	var local18: Color = null;
	var local19: Color = null;
	var local29: Color = null;
}

object Theme {
	private val theme = new Theme();
	private val device = Display.getCurrent();
	theme.local18 = new Color(device, 54, 54, 178);
	theme.local19 = new Color(device, 42, 140, 42);
	theme.local29 = new Color(device, 176, 55, 176);
	theme.messageForeground = new Color(device, 0, 0, 0);
	theme.messageBackground = new Color(device, 240, 240, 240);
	theme.selectionForeground = new Color(device, 255, 255, 255);
	theme.selectionBackground = new Color(device, 53, 110, 193);
	theme.newData = new Color(device, 153, 0, 0);
	theme.newMessage = new Color(device, 255, 0, 0);
	val current = theme;
}