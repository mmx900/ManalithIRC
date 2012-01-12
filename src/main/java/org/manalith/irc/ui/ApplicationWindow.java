package org.manalith.irc.ui;

import org.apache.commons.configuration.Configuration;
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
import org.manalith.irc.Application;
import org.manalith.irc.model.Connection;
import org.manalith.irc.model.Server;

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
		shell.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				application.disconnectAllConnection();
			}
		});
		shell.setSize(450, 300);
		shell.setText("Manalith IRC");
		shell.setLayout(new BorderLayout(0, 0));

		final TabFolder tabFolder = new TabFolder(shell, SWT.BORDER);
		tabFolder.setLayoutData(BorderLayout.CENTER);

		Menu menu = new Menu(shell, SWT.BAR);
		shell.setMenuBar(menu);

		ToolBar toolBar = new ToolBar(shell, SWT.FLAT | SWT.RIGHT);
		toolBar.setLayoutData(BorderLayout.NORTH);

		ToolItem tltmConnect = new ToolItem(toolBar, SWT.NONE);
		tltmConnect.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Configuration config = application.getConfiguration();
				Server server = new Server();
				server.setHostname(config.getString("server.host"));
				server.setPort(config.getInt("server.port"));
				server.setEncoding(config.getString("server.encoding"));
				server.setVerbose(config.getBoolean("server.verbose"));
				server.setNickname(config.getString("server.nickname"));
				server.setDefaultChannels(config
						.getList("server.defaultChannels"));

				TabItem tbtmChannel = new TabItem(tabFolder, SWT.NONE);
				tbtmChannel.setText(server.getHostname());

				ChannelComposite composite = new ChannelComposite(tabFolder,
						SWT.NONE);
				tbtmChannel.setControl(composite);

				Connection connection = application.createConnection(server);
				composite.setConnection(connection);

				if (connection.connect()) {
					
				}
			}
		});
		tltmConnect.setText("Connect");

		ToolItem tltmJoin = new ToolItem(toolBar, SWT.NONE);
		tltmJoin.setText("Join");

		ToolItem tltmProperties = new ToolItem(toolBar, SWT.NONE);
		tltmProperties.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				new PropertyShell(display, application).open();
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
}
