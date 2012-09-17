package org.manalith.irc.ui;

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Set
import scala.collection.JavaConversions.asScalaSet
import scala.collection.mutable.Publisher
import scala.collection.mutable.Subscriber
import org.apache.commons.lang3.StringUtils
import org.eclipse.swt.SWT
import org.eclipse.swt.events.KeyEvent
import org.eclipse.swt.events.KeyListener
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
import org.manalith.irc.helper.LogHelper
import org.manalith.irc.helper.SwtUtil

class ChannelView(parent: Composite, style: Int, val channel: Channel, val connection: Connection)
	extends Composite(parent, style)
	with IrcTab with Publisher[Action] with LogHelper {
	val EVENT_MESSAGE_SUBMITTED = "MessageSubmitted";

	setLayout(new BorderLayout(0, 0));
	var messageOutput: MessageList = null;
	val userList = new UserList(this, SWT.BORDER, channel);
	private var topic: Text = null;
	var messageInput: Text = null;

	{
		userList.setLayoutData(BorderLayout.EAST);

		messageOutput = new MessageList(this, SWT.BORDER);
		messageOutput.setLayoutData(BorderLayout.CENTER);

		topic = new Text(this, SWT.BORDER);
		topic.setLayoutData(BorderLayout.NORTH);
		topic.setText(channel.getTopic());

		printMessage("*", String.format("당신은 대화방 %s에 참여합니다.", channel.getName()));
		printMessage("*", String.format("대화방 %s의 주제는 %s 입니다.", channel.getName(),
			channel.getTopic()));
		printMessage("*", String.format("대화방 %s의 주제는 %s님이 설정했습니다. (시간: %s)", channel
			.getName(), channel.getTopicSetter(), new SimpleDateFormat()
			.format(new Date(channel.getTopicTimestamp()))));

		messageInput = new Text(this, SWT.BORDER);
		messageInput.setLayoutData(BorderLayout.SOUTH);
		messageInput.addKeyListener(new KeyListener() {
			def keyPressed(e: KeyEvent) {
				if (e.character == SWT.CR) {
					publish(new Action(EVENT_MESSAGE_SUBMITTED, messageInput,
						ChannelView.this));
				}
			}

			def keyReleased(e: KeyEvent) {
				// ignore
			}
		});

		connection.addEventListener(new ChannelEventDispatcher());
		subscribe(new ActionAdapter());
	}

	def printMessage(actor: String, message: String) = {
		messageOutput.append(actor, message, false);
	}

	def printMessage(actor: String, message: String, highlight: Boolean) = {
		messageOutput.append(actor, message, highlight);
	}

	def setTopic(topic: String) = {
		this.topic.setText(topic);
	}

	def updateUserList() {
		userList.updateList();
	}

	private class ActionAdapter extends Subscriber[Action, Publisher[Action]] {
		def notify(pub: Publisher[Action], action: Action) = {
			action.command match {
				case EVENT_MESSAGE_SUBMITTED => {
					var message = messageInput.getText();
					if (StringUtils.isNotBlank(message)) {
						connection.sendMessage(channel.getName(), message);
						messageInput.setText("");
						printMessage(connection.nick, message);
					}
				}
			}
		}
	}

	private class ChannelEventDispatcher extends ListenerAdapter[PircBotX] {
		@throws(classOf[Exception])
		override def onAction(event: ActionEvent[PircBotX]) {
			if (event.getChannel().getName() == channel.getName()) {
				SwtUtil.asyncExec(printMessage("*", String.format("%1s %2s", event.getUser()
					.getNick(), event.getMessage())));
			}
		}

		@throws(classOf[Exception])
		override def onPart(event: PartEvent[PircBotX]) {
			val nick = event.getUser().getNick();

			if (channel.getName() == event.getChannel().getName() && nick != connection.nick) {
				SwtUtil.asyncExec({
					printMessage("*", String.format("%1s 님이 퇴장하셨습니다. (%2s)",
						nick, event.getReason()));
					updateUserList();
				});
			}
		}

		@throws(classOf[Exception])
		override def onMessage(event: MessageEvent[PircBotX]) {
			if (event.getChannel() != null
				&& event.getChannel().getName() == channel.getName()) {
				if (event.getMessage().contains(connection.nick)) {
					SwtUtil.asyncExec(printMessage(event.getUser().getNick(), event.getMessage(), true));
				} else {
					SwtUtil.asyncExec(printMessage(event.getUser().getNick(), event.getMessage()));
				}
			}
		}

		@throws(classOf[Exception])
		override def onJoin(event: JoinEvent[PircBotX]) {
			val nick = event.getUser().getNick();

			if (channel.getName() == event.getChannel().getName()
				&& nick != connection.nick) {
				SwtUtil.asyncExec({
					printMessage("*", String.format("%1s 님이 입장하셨습니다.", nick));
					updateUserList();
				});
			}
		}

		@throws(classOf[Exception])
		override def onQuit(event: QuitEvent[PircBotX]) {
			if (event.getUser().getChannels().contains(channel)) {
				SwtUtil.asyncExec({
					printMessage("*", String.format("%1s 님이 종료하셨습니다. (%2s)",
						event.getUser().getNick(), event.getReason()));
				});
			}
		}
	}
}
