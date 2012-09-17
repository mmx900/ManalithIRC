package org.manalith.irc.ui

import org.eclipse.swt.widgets.Table
import org.eclipse.swt.widgets.Composite
import org.eclipse.swt.widgets.TableItem
import org.eclipse.swt.SWT
import org.eclipse.swt.widgets.TableColumn
import org.eclipse.jface.layout.TableColumnLayout
import org.eclipse.jface.viewers.ColumnWeightData
import org.eclipse.swt.graphics.Color
import java.util.Date
import java.text.SimpleDateFormat
import org.manalith.irc.model.Theme
import org.eclipse.jface.viewers.TableViewer
import org.eclipse.swt.widgets.Listener
import org.eclipse.swt.widgets.Event
import org.eclipse.swt.graphics.TextLayout
import org.eclipse.swt.widgets.Display
import org.eclipse.jface.viewers.ColumnPixelData
import org.manalith.irc.helper.LogHelper
import org.eclipse.swt.events.ControlAdapter
import org.eclipse.swt.events.ControlEvent

class MessageList(parent: Composite, style: Int) extends Composite(parent, style) with LogHelper {
	val viewLayout = new TableColumnLayout();
	setLayout(viewLayout);
	//val tableViewer = new TableViewer(this, SWT.V_SCROLL);
	val table = new Table(this, SWT.V_SCROLL);
	//val table = tableViewer.getTable();
	table.setBackground(Theme.current.messageBackground);
	table.setForeground(Theme.current.messageForeground);
	table.setRedraw(false);
	table.setHeaderVisible(false);
	table.setLinesVisible(false);
	table.setRedraw(true);

	val colTime = new TableColumn(table, SWT.NONE);
	val colNick = new TableColumn(table, SWT.RIGHT);
	val colMessage = new TableColumn(table, SWT.NONE);

	viewLayout.setColumnData(colTime, new ColumnPixelData(50, false));
	viewLayout.setColumnData(colNick, new ColumnPixelData(120, false));
	viewLayout.setColumnData(colMessage, new ColumnPixelData(400, true, true));

	val dateFormat = new SimpleDateFormat("HH:mm");

	val textLayout = new TextLayout(Display.getDefault());

	//XXX 리사이즈시 Row height 변경 안 되는 문제 : https://bugs.eclipse.org/bugs/show_bug.cgi?id=130024
	//XXX 단순히 Line Wrap을 위해 테이블 내용을 직접 그리는 문제 : https://bugs.eclipse.org/bugs/show_bug.cgi?id=195597 
	table.addListener(SWT.PaintItem, new Listener() {
		override def handleEvent(event: Event) {
			if (event.index == 2) {
				val item = event.item.asInstanceOf[TableItem];
				val text = item.getText(event.index);
				textLayout.setText(text)
				textLayout.draw(event.gc, event.x, event.y);
			}
		}
	});

	table.addListener(SWT.MeasureItem, new Listener() {
		override def handleEvent(event: Event) {
			if (event.index == 2) {
				val item = event.item.asInstanceOf[TableItem];
				val text = item.getText(event.index);
				textLayout.setText(text)

				event.height = textLayout.getBounds().height + 2;
			}
		}
	});

	table.addListener(SWT.EraseItem, new Listener() {
		override def handleEvent(event: Event) {
			if (event.index == 2) {
				event.detail &= ~SWT.FOREGROUND;
			}
		}
	});

	table.addControlListener(new ControlAdapter() {
		override def controlResized(e: ControlEvent) = {
			if (colMessage.getWidth() > 0) {
				textLayout.setWidth(colMessage.getWidth());
			}
		}
	});

	def append(actor: String, message: String, highlight: Boolean) = {
		setRedraw(false);
		val item = new TableItem(table, SWT.NONE);
		item.setText(0, dateFormat.format(new Date()));
		item.setText(1, actor);

		var newMessage = message;

		item.setText(2, newMessage);

		if (highlight) {
			item.setForeground(1, Theme.current.newMessage);
			item.setForeground(2, Theme.current.newMessage);
		} else {
			item.setForeground(1, Theme.current.local18);
		}

		setRedraw(true);
		table.setTopIndex(table.getItemCount() - 1);
	}
}