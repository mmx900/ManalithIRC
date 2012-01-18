package org.manalith.irc.ui;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Text;

import swing2swt.layout.BorderLayout;

public class ChannelView extends Composite implements IrcTab {
	private java.util.List<ActionListener> actionListeners = new ArrayList<ActionListener>();
	private StyledText messageOutput;
	private List list;
	private Text topic;
	private Text messageInput;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public ChannelView(Composite parent, int style) {
		super(parent, style);
		setLayout(new BorderLayout(0, 0));

		list = new List(this, SWT.BORDER | SWT.MULTI);
		list.setLayoutData(BorderLayout.EAST);

		messageOutput = new StyledText(this, SWT.BORDER | SWT.H_SCROLL
				| SWT.V_SCROLL);
		messageOutput.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				messageOutput.setTopIndex(messageOutput.getLineCount() - 1);
			}
		});

		messageOutput.setLayoutData(BorderLayout.CENTER);

		topic = new Text(this, SWT.BORDER);
		topic.setLayoutData(BorderLayout.NORTH);

		messageInput = new Text(this, SWT.BORDER);
		messageInput.setLayoutData(BorderLayout.SOUTH);

	}

	public StyledText getMessageOutput() {
		return messageOutput;
	}

	public List getUserList() {
		return list;
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
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
