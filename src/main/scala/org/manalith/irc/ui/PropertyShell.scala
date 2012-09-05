package org.manalith.irc.ui;

import org.eclipse.swt.SWT
import org.eclipse.swt.layout.RowLayout
import org.eclipse.swt.widgets.Button
import org.eclipse.swt.widgets.Combo
import org.eclipse.swt.widgets.Composite
import org.eclipse.swt.widgets.Display
import org.eclipse.swt.widgets.Group
import org.eclipse.swt.widgets.Shell
import org.eclipse.swt.widgets.TabFolder
import org.eclipse.swt.widgets.TabItem

class PropertyShell(display: Display) extends Shell(display, SWT.SHELL_TRIM) {
	createContents;

	/**
	 * Create contents of the shell.
	 */
	private def createContents = {
		setText("Properties");
		setSize(450, 300);

		val rowLayout = new RowLayout(SWT.VERTICAL);
		rowLayout.fill = true;
		setLayout(rowLayout);

		var tabFolder = new TabFolder(this, SWT.BORDER);

		var tbtmNetworks = new TabItem(tabFolder, SWT.NONE);
		tbtmNetworks.setText("Networks");

		var grpServers = new Group(tabFolder, SWT.NONE);
		grpServers.setText("Servers");
		tbtmNetworks.setControl(grpServers);
		grpServers.setLayout(new RowLayout(SWT.VERTICAL));

		var combo = new Combo(grpServers, SWT.NONE);

		var composite_1 = new Composite(grpServers, SWT.NONE);
		composite_1.setLayout(new RowLayout(SWT.HORIZONTAL));

		var btnNewButton_2 = new Button(composite_1, SWT.NONE);
		btnNewButton_2.setText("Add");

		var btnNewButton_3 = new Button(composite_1, SWT.NONE);
		btnNewButton_3.setText("Remove");

		var composite = new Composite(this, SWT.NONE);
		composite.setLayout(new RowLayout(SWT.HORIZONTAL));

		var btnNewButton = new Button(composite, SWT.NONE);
		btnNewButton.setText("Help");

		var btnNewButton_1 = new Button(composite, SWT.NONE);
		btnNewButton_1.setText("Close");
	}

	override def checkSubclass = {
		// Disable the check that prevents subclassing of SWT components
	}
}
