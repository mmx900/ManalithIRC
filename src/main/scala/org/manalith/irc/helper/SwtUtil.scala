package org.manalith.irc.helper

import org.apache.log4j.Logger
import org.eclipse.swt.widgets.Shell
import org.eclipse.swt.widgets.Display

object SwtUtil {
	def centerInDisplay(shell: Shell) {
		val displayArea = shell.getDisplay().getClientArea();
		shell.setBounds(displayArea.width / 4, displayArea.height / 4,
			displayArea.width / 2, displayArea.height / 2);
	}

	def asyncExec(statement: => Unit) = {
		Display.getDefault().asyncExec(new Runnable() {
			def run = {
				statement
			}
		});
	}
}