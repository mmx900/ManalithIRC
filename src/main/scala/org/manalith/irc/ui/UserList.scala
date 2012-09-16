package org.manalith.irc.ui

import java.util.Set

import scala.collection.JavaConversions.asScalaSet

import org.eclipse.jface.layout.TableColumnLayout
import org.eclipse.jface.viewers.ColumnWeightData
import org.eclipse.swt.SWT
import org.eclipse.swt.widgets.Composite
import org.eclipse.swt.widgets.Table
import org.eclipse.swt.widgets.TableColumn
import org.eclipse.swt.widgets.TableItem
import org.pircbotx.User

class UserList(parent: Composite, style: Int) extends Composite(parent, style) {
	val viewLayout = new TableColumnLayout();
	setLayout(viewLayout);

	val table = new Table(this, SWT.V_SCROLL);
	table.setRedraw(false);
	table.setHeaderVisible(false);
	table.setLinesVisible(false);
	table.setRedraw(true);

	val colType = new TableColumn(table, SWT.NONE);
	val colNick = new TableColumn(table, SWT.NONE);

	viewLayout.setColumnData(colType, new ColumnWeightData(10, 10, false));
	viewLayout.setColumnData(colNick, new ColumnWeightData(120, 120, false));

	def setUsers(users: Set[User]) {
		setRedraw(false);
		table.removeAll();

		for (u <- users) {
			val item = new TableItem(table, SWT.NONE);
			item.setText(1, u.getNick());
		}
		
		setRedraw(true);
	}

}