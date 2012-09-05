package org.manalith.irc.ui;

import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Date
import java.util.Set

import scala.collection.JavaConversions.asScalaBuffer
import scala.collection.JavaConversions.asScalaSet

import org.eclipse.swt.SWT
import org.eclipse.swt.custom.StyleRange
import org.eclipse.swt.custom.StyledText
import org.eclipse.swt.events.KeyEvent
import org.eclipse.swt.events.KeyListener
import org.eclipse.swt.events.ModifyEvent
import org.eclipse.swt.events.ModifyListener
import org.eclipse.swt.graphics.Color
import org.eclipse.swt.widgets.Composite
import org.eclipse.swt.widgets.Display
import org.eclipse.swt.widgets.List
import org.eclipse.swt.widgets.Text
import org.manalith.irc.model.Connection
import org.pircbotx.Channel
import org.pircbotx.PircBotX
import org.pircbotx.User
import org.pircbotx.hooks.ListenerAdapter
import org.pircbotx.hooks.events.ActionEvent
import org.pircbotx.hooks.events.JoinEvent
import org.pircbotx.hooks.events.MessageEvent
import org.pircbotx.hooks.events.PartEvent
import org.pircbotx.hooks.events.QuitEvent

import swing2swt.layout.BorderLayout

class ChannelView(parent: Composite, style: Int, channel: Channel, private val connection: Connection) extends Composite(parent, style) with IrcTab {
	val EVENT_MESSAGE_SUBMITTED = "MessageSubmitted";

	private val actionListeners: java.util.List[ActionListener] = new ArrayList[ActionListener]();

	setLayout(new BorderLayout(0, 0));
	var messageOutput: StyledText = null;
	val userList: List = new List(this, SWT.BORDER | SWT.MULTI);
	private var topic: Text = null;
	var messageInput: Text = null;
	var channelName: String = channel.getName();
	private var highlightColor: Color = null;

	{
		userList.setLayoutData(BorderLayout.EAST);

		messageOutput = new StyledText(this, SWT.BORDER | SWT.V_SCROLL
			| SWT.WRAP);
		messageOutput.addModifyListener(new ModifyListener() {
			def modifyText(e: ModifyEvent) = {
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
		printMessage(String.format("대화방 %s의 주제는 %s님이 설정했습니다. (시간: %s)", channel
			.getName(), channel.getTopicSetter(), new SimpleDateFormat()
			.format(new Date(channel.getTopicTimestamp()))));

		messageInput = new Text(this, SWT.BORDER);
		messageInput.setLayoutData(BorderLayout.SOUTH);
		messageInput.addKeyListener(new KeyListener() {
			def keyPressed(e: KeyEvent) {
				if (e.character == SWT.CR) {
					onAction(new Action(EVENT_MESSAGE_SUBMITTED, messageInput,
						ChannelView.this));
				}
			}

			def keyReleased(e: KeyEvent) {
				// ignore
			}
		});

		connection.addEventListener(new ChannelEventDispatcher());
		addActionListener(new ActionAdapter());

		highlightColor = getDisplay().getSystemColor(SWT.COLOR_RED);
	}

	def printMessage(message: String) = {
		messageOutput.append(message + "\n");
	}

	def printMessage(message: String, color: Color) = {
		val styleStart = messageOutput.getText().length();
		val styleLength = message.length() + 1;

		val ranges = new ArrayList[StyleRange]();
		ranges.add(new StyleRange(styleStart, styleLength, color, null,
			SWT.BOLD));
		messageOutput.append(message + "\n");
		messageOutput.replaceStyleRanges(styleStart, styleLength,
			ranges.toArray(new Array[StyleRange](0)));
	}

	def printAsyncMessage(message: String) {
		Display.getDefault().asyncExec(new Runnable() {
			def run = {
				printMessage(message);
			}
		});
	}

	def printAsyncMessage(message: String, color: Color) {
		Display.getDefault().asyncExec(new Runnable() {
			def run = {
				printMessage(message, color);
			}
		});
	}

	override def checkSubclass = {
		// Disable the check that prevents subclassing of SWT components
	}

	def addActionListener(listener: ActionListener) {
		actionListeners.add(listener);
	}

	def removeActionListener(listener: ActionListener) {
		actionListeners.remove(listener);
	}

	private def onAction(action: Action) {
		for (listener <- actionListeners) {
			listener.onAction(action);
		}
	}

	def setTopic(topic: String) = {
		this.topic.setText(topic);
	}

	def updateUserList(users: Set[User]) {
		for (u <- users) {
			userList.add(u.getNick());
		}
		userList.redraw();
	}

	private class ActionAdapter extends ActionListener {
		def onAction(action: Action) {
			action.command match {
				case EVENT_MESSAGE_SUBMITTED => {
					var message = messageInput.getText();
					connection.sendMessage(channelName, message);
					messageInput.setText("");
					printMessage(String.format("<%1s> %2s", connection.nick,
						message));
				}
			}
		}
	}

	private class ChannelEventDispatcher extends ListenerAdapter[PircBotX] {
		@throws(classOf[Exception])
		override def onAction(event: ActionEvent[PircBotX]) {
			printAsyncMessage(String.format("\t\t %1s %2s", event.getUser()
				.getNick(), event.getMessage()));
		}

		@throws(classOf[Exception])
		override def onPart(event: PartEvent[PircBotX]) {
			Display.getDefault().asyncExec(new Runnable() {
				def run() {
					val nick = event.getUser().getNick();

					if (nick.equals(connection.nick)) {
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

		@throws(classOf[Exception])
		override def onMessage(event: MessageEvent[PircBotX]) {
			if (event.getChannel() != null
				&& event.getChannel().getName().equals(channelName)) {
				if (event.getMessage().contains(connection.nick)) {
					printAsyncMessage(String.format("<%1s> %2s", event
						.getUser().getNick(), event.getMessage()),
						highlightColor);
				} else {
					printAsyncMessage(String.format("<%1s> %2s", event
						.getUser().getNick(), event.getMessage()));
				}
			}
		}

		@throws(classOf[Exception])
		override def onJoin(event: JoinEvent[PircBotX]) {
			val nick = event.getUser().getNick();

			if (channelName.equals(event.getChannel().getName())
				&& !nick.equals(connection.nick)) {
				printMessage(String.format("%1s 님이 입장하셨습니다.", nick));
			}
		}

		@throws(classOf[Exception])
		override def onQuit(event: QuitEvent[PircBotX]) {
			Display.getDefault().asyncExec(new Runnable() {
				def run() {
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
