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
import org.pircbotx.Channel

class UserList(parent: Composite, style: Int, channel: Channel) extends Composite(parent, style) {
	val viewLayout = new TableColumnLayout();
	setLayout(viewLayout);

	val table = new Table(this, SWT.V_SCROLL);
	table.setRedraw(false);
	table.setHeaderVisible(false);
	table.setLinesVisible(false);
	table.setRedraw(true);

	val colPermission = new TableColumn(table, SWT.NONE);
	val colNick = new TableColumn(table, SWT.NONE);

	viewLayout.setColumnData(colPermission, new ColumnWeightData(20, 20, false));
	viewLayout.setColumnData(colNick, new ColumnWeightData(120, 120, false));

	def updateList() {
		setRedraw(false);
		table.removeAll();

		for (u <- channel.getOps()) {
			appendUser("o", u.getNick());
		}

		for (u <- channel.getVoices()) {
			appendUser("v", u.getNick());
		}

		for (u <- channel.getSuperOps()) {
			appendUser("s", u.getNick());
		}

		for (u <- channel.getHalfOps()) {
			appendUser("h", u.getNick());
		}

		for (u <- channel.getOwners()) {
			appendUser("w", u.getNick());
		}

		for (u <- channel.getNormalUsers()) {
			appendUser("", u.getNick());
		}

		setRedraw(true);
	}

	private def appendUser(permission: String, nick: String) {
		val item = new TableItem(table, SWT.NONE);
		item.setText(0, permission);
		item.setText(1, nick);
	}

}