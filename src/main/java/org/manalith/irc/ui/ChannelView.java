package org.manalith.irc.ui;

import java.util.ArrayList;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Text;
import org.manalith.irc.model.Connection;
import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.ActionEvent;
import org.pircbotx.hooks.events.JoinEvent;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.PartEvent;
import org.pircbotx.hooks.events.QuitEvent;

import swing2swt.layout.BorderLayout;

public class ChannelView extends Composite implements IrcTab {
	public static final String EVENT_MESSAGE_SUBMITTED = "MessageSubmitted";

	private java.util.List<ActionListener> actionListeners = new ArrayList<ActionListener>();
	private StyledText messageOutput;
	private List list;
	private Text topic;
	private Text messageInput;
	private String channelName;
	private Connection connection;
	private Color highlightColor;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public ChannelView(Composite parent, int style, Channel channel,
			Connection connection) {
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
		topic.setText(channel.getTopic());

		printMessage(String.format("당신은 대화방 %s에 참여합니다.", channel.getName()));
		printMessage(String.format("대화방 %s의 주제는 %s 입니다.", channel.getName(),
				channel.getTopic()));
		printMessage(String.format("대화방 %s의 주제는 %s님이 설정했습니다. (시간: %d)",
				channel.getName(), channel.getTopicSetter(),
				channel.getTopicTimestamp()));

		messageInput = new Text(this, SWT.BORDER);
		messageInput.setLayoutData(BorderLayout.SOUTH);
		messageInput.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent e) {
				if (e.character == SWT.CR) {
					onAction(new Action(EVENT_MESSAGE_SUBMITTED, messageInput,
							ChannelView.this));
				}
			}

			public void keyReleased(KeyEvent e) {
				// ignore
			}
		});

		this.channelName = channel.getName();
		this.connection = connection;

		connection.addEventListener(new ChannelEventDispatcher());
		addActionListener(new ActionAdapter());

		highlightColor = getDisplay().getSystemColor(SWT.COLOR_RED);
	}

	public Text getMessageInput() {
		return messageInput;
	}

	public StyledText getMessageOutput() {
		return messageOutput;
	}

	public void printMessage(String message) {
		messageOutput.append(message + "\n");
	}

	public void printMessage(String message, Color color) {
		int styleStart = messageOutput.getText().length();
		int styleLength = message.length() + 1;

		java.util.List<StyleRange> ranges = new ArrayList<StyleRange>();
		ranges.add(new StyleRange(styleStart, styleLength, color, null,
				SWT.BOLD));
		messageOutput.append(message + "\n");
		messageOutput.replaceStyleRanges(styleStart, styleLength,
				ranges.toArray(new StyleRange[0]));
	}

	public void printAsyncMessage(final String message) {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				printMessage(message);
			}
		});
	}

	public void printAsyncMessage(final String message, final Color color) {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				printMessage(message, color);
			}
		});
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

	public String getChannelName() {
		return channelName;
	}

	public void setTopic(String topic) {
		this.topic.setText(topic);
	}

	public void updateUserList(final Set<User> users) {
		for (User u : users) {
			getUserList().add(u.getNick());
		}
		getUserList().redraw();
	}

	private class ActionAdapter implements ActionListener {
		public void onAction(Action action) {
			switch (action.getCommand()) {
			case EVENT_MESSAGE_SUBMITTED: {
				String message = getMessageInput().getText();
				connection.sendMessage(getChannelName(), message);
				getMessageInput().setText("");
				printMessage(String.format("<%1s> %2s", connection.getNick(),
						message));
				break;
			}
			}
		}
	}

	private class ChannelEventDispatcher extends ListenerAdapter<PircBotX> {
		public void onAction(final ActionEvent<PircBotX> event)
				throws Exception {
			printAsyncMessage(String.format("\t\t %1s %2s", event.getUser()
					.getNick(), event.getMessage()));
		}

		public void onPart(final PartEvent<PircBotX> event) throws Exception {
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					String nick = event.getUser().getNick();

					if (nick.equals(connection.getNick())) {
						// TODO
						/*
						 * TabItem channelTab =
						 * window.createChannelTab(channelName, channelName,
						 * connection); ChannelView view = (ChannelView)
						 * channelTab.getControl();
						 * view.addActionListener(instance);
						 * connection.getChannelViewList().add(view);
						 */
					} else {
						printMessage(String.format("%1s 님이 퇴장하셨습니다. (%2s)",
								nick, event.getReason()));
					}
				}
			});
		}

		public void onMessage(MessageEvent<PircBotX> event) throws Exception {
			if (event.getChannel() != null
					&& event.getChannel().getName().equals(channelName)) {
				if (event.getMessage().contains(connection.getNick())) {
					printAsyncMessage(String.format("<%1s> %2s", event
							.getUser().getNick(), event.getMessage()),
							highlightColor);
				} else {
					printAsyncMessage(String.format("<%1s> %2s", event
							.getUser().getNick(), event.getMessage()));
				}
			}
		}

		public void onJoin(final JoinEvent<PircBotX> event) throws Exception {
			String nick = event.getUser().getNick();

			if (channelName.equals(event.getChannel().getName())
					&& !nick.equals(connection.getNick())) {
				printMessage(String.format("%1s 님이 입장하셨습니다.", nick));
			}
		}

		public void onQuit(final QuitEvent<PircBotX> event) throws Exception {
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					// TODO 셀프일 경우 닫기, 타인일 경우 표시
					/*
					 * Connection connection = getConnection(event.getBot()
					 * .getServer()); String channelName =
					 * event.getChannel().getName(); String nick =
					 * event.getUser().getNick();
					 * 
					 * if (nick.equals(connection.getNick())) { // TODO } else {
					 * ChannelView view =
					 * connection.getChannelView(channelName);
					 * view.printMessage(String.format("%1s 님이 종료하셨습니다. (%2s)",
					 * nick, event.getReason())); }
					 */
				}
			});
		}
	}
}
