package org.manalith.irc.ui

import java.text.SimpleDateFormat
import java.util.ArrayList
import org.eclipse.swt.SWT
import org.eclipse.swt.custom.StyleRange
import org.eclipse.swt.custom.StyledText
import org.eclipse.swt.events.ModifyEvent
import org.eclipse.swt.events.ModifyListener
import org.eclipse.swt.widgets.Composite
import org.manalith.irc.model.Theme
import org.eclipse.swt.layout.FillLayout
import java.util.Date

class AlternativeMessageList(parent: Composite, style: Int) extends Composite(parent, style) {
	setLayout(new FillLayout());
	val text = new StyledText(this, SWT.BORDER | SWT.V_SCROLL
		| SWT.WRAP);
	text.addModifyListener(new ModifyListener() {
		def modifyText(e: ModifyEvent) = {
			text.setTopIndex(text.getLineCount() - 1);
		}
	});

	val dateFormat = new SimpleDateFormat("HH:mm");

	def append(actor: String, message: String, highlight: Boolean) = {
		val newMessage = "[%1s] <%2s> %3s".format(dateFormat.format(new Date()), actor, message + "\n");
		
		if (highlight) {
			val styleStart = text.getText().length();
			val styleLength = newMessage.length();

			val ranges = new ArrayList[StyleRange]();
			ranges.add(new StyleRange(styleStart, styleLength, Theme.current.newMessage, null,
				SWT.BOLD));
			text.append(newMessage);

			text.replaceStyleRanges(styleStart, styleLength,
				ranges.toArray(new Array[StyleRange](0)));
		} else {
			text.append(newMessage);
		}
	}
}