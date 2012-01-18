package org.manalith.irc.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import swing2swt.layout.BorderLayout;

public class ApplicationWindow {
	public static final String EVENT_WINDOW_DISPOSED = "WindowDisposed";

	public static final String EVENT_CONNECT_BUTTON_CLICKED = "ConnectButtonClicked";

	private List<ActionListener> actionListeners = new ArrayList<ActionListener>();
	private TabFolder tabFolder;

	public ApplicationWindow() {
	}

	/**
	 * Open the window.
	 * 
	 * @wbp.parser.entryPoint
	 */
	public void open() {
		final Display display = Display.getDefault();
		final Shell shell = new Shell();
		shell.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				onAction(new Action(EVENT_WINDOW_DISPOSED, shell));
			}
		});
		shell.setSize(450, 300);
		shell.setText("Manalith IRC");
		shell.setLayout(new BorderLayout(0, 0));

		tabFolder = new TabFolder(shell, SWT.BORDER);
		tabFolder.setLayoutData(BorderLayout.CENTER);

		Menu menu = new Menu(shell, SWT.BAR);
		shell.setMenuBar(menu);

		ToolBar toolBar = new ToolBar(shell, SWT.FLAT | SWT.RIGHT);
		toolBar.setLayoutData(BorderLayout.NORTH);

		final ToolItem tltmConnect = new ToolItem(toolBar, SWT.NONE);
		tltmConnect.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				onAction(new Action(EVENT_CONNECT_BUTTON_CLICKED, tltmConnect));
			}
		});
		tltmConnect.setText("Connect");

		ToolItem tltmJoin = new ToolItem(toolBar, SWT.NONE);
		tltmJoin.setText("Join");

		ToolItem tltmProperties = new ToolItem(toolBar, SWT.NONE);
		tltmProperties.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				new PropertyShell(display).open();
			}
		});
		tltmProperties.setText("Properties");

		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	public TabItem createServerTab(String title) {
		TabItem item = new TabItem(tabFolder, SWT.NONE);
		item.setText(title);
		ServerMessageView view = new ServerMessageView(tabFolder, SWT.NONE);
		item.setControl(view);
		return item;
	}

	public TabItem createChannelTab(String title) {
		TabItem item = new TabItem(tabFolder, SWT.NONE);
		item.setText(title);
		ChannelView view = new ChannelView(tabFolder, SWT.NONE);
		item.setControl(view);
		return item;
	}

	public void closeTab(IrcTab tab) {

	}

	public void addActionListener(ActionListener listener) {
		actionListeners.add(listener);
	}

	public void removeActionListener(ActionListener listener) {
		actionListeners.remove(listener);
	}

	private void onAction(Action action) {
		for (ActionListener listener : actionListeners) {
			listener.onAction(action);
		}
	}
}
