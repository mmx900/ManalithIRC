package org.manalith.irc.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Combo;
import org.manalith.irc.Application;

public class PropertyShell extends Shell {

	/**
	 * Create the shell.
	 * @param display
	 * @param application 
	 */
	public PropertyShell(Display display, Application application) {
		super(display, SWT.SHELL_TRIM);
		createContents();
	}

	/**
	 * Create contents of the shell.
	 */
	protected void createContents() {
		setText("Properties");
		setSize(450, 300);

		RowLayout rowLayout = new RowLayout(SWT.VERTICAL);
		rowLayout.fill = true;
		setLayout(rowLayout);
		
		CTabFolder tabFolder = new CTabFolder(this, SWT.BORDER);
		tabFolder.setSelectionBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));
		
		CTabItem tbtmNetworks = new CTabItem(tabFolder, SWT.NONE);
		tbtmNetworks.setText("Networks");
		
		Group grpServers = new Group(tabFolder, SWT.NONE);
		grpServers.setText("Servers");
		tbtmNetworks.setControl(grpServers);
		grpServers.setLayout(new RowLayout(SWT.VERTICAL));
		
		Combo combo = new Combo(grpServers, SWT.NONE);
		
		Composite composite_1 = new Composite(grpServers, SWT.NONE);
		composite_1.setLayout(new RowLayout(SWT.HORIZONTAL));
		
		Button btnNewButton_2 = new Button(composite_1, SWT.NONE);
		btnNewButton_2.setText("Add");
		
		Button btnNewButton_3 = new Button(composite_1, SWT.NONE);
		btnNewButton_3.setText("Remove");
		
		Composite composite = new Composite(this, SWT.NONE);
		composite.setLayout(new RowLayout(SWT.HORIZONTAL));
		
		Button btnNewButton = new Button(composite, SWT.NONE);
		btnNewButton.setText("Help");
		
		Button btnNewButton_1 = new Button(composite, SWT.NONE);
		btnNewButton_1.setText("Close");
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
