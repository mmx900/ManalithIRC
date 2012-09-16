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

class MessageList(parent: Composite, style: Int) extends Composite(parent, style) {
	val viewLayout = new TableColumnLayout();
	setLayout(viewLayout);

	val table = new Table(this, SWT.V_SCROLL);
	table.setRedraw(false);
	table.setHeaderVisible(false);
	table.setLinesVisible(false);
	table.setRedraw(true);

	val colTime = new TableColumn(table, SWT.NONE);
	val colNick = new TableColumn(table, SWT.RIGHT);
	val colMessage = new TableColumn(table, SWT.NONE);

	viewLayout.setColumnData(colTime, new ColumnWeightData(50, 50, false));
	viewLayout.setColumnData(colNick, new ColumnWeightData(120, 120, false));
	viewLayout.setColumnData(colMessage, new ColumnWeightData(400, 400, true));

	val dateFormat = new SimpleDateFormat("hh:mm");

	def append(actor: String, message: String, color: Color) = {

		setRedraw(false);
		val item = new TableItem(table, SWT.NONE);
		item.setText(0, dateFormat.format(new Date()));
		item.setText(1, actor);
		item.setText(2, message);

		if (color != null) {
			item.setForeground(1, color);
			item.setForeground(2, color);
		}

		setRedraw(true);
		table.setTopIndex(table.getItemCount() - 1);
	}
}