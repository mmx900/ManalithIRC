package org.manalith.irc.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.manalith.irc.Application;

import swing2swt.layout.BorderLayout;

public class ApplicationWindow {
	private Application application;

	public ApplicationWindow(Application application) {
		this.application = application;
	}

	/**
	 * Open the window.
	 * 
	 * @wbp.parser.entryPoint
	 */
	public void open() {
		final Display display = Display.getDefault();
		Shell shell = new Shell();
		shell.setSize(450, 300);
		shell.setText("SWT Application");
		shell.setLayout(new BorderLayout(0, 0));

		Menu menu = new Menu(shell, SWT.BAR);
		shell.setMenuBar(menu);

		ToolBar toolBar = new ToolBar(shell, SWT.FLAT | SWT.RIGHT);
		toolBar.setLayoutData(BorderLayout.NORTH);

		ToolItem tltmConnect = new ToolItem(toolBar, SWT.NONE);
		tltmConnect.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				new PropertyShell(display, application).open();
			}
		});
		tltmConnect.setText("Connect");

		ToolItem tltmJoin = new ToolItem(toolBar, SWT.NONE);
		tltmJoin.setText("Join");

		TabFolder tabFolder = new TabFolder(shell, SWT.BORDER);
		tabFolder.setLayoutData(BorderLayout.CENTER);

		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}
}
