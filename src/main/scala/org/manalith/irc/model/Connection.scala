package org.manalith.irc.model;

import java.io.IOException
import java.lang.Runnable

import scala.collection.JavaConversions.asScalaBuffer

import org.apache.log4j.Logger
import org.eclipse.swt.widgets.Display
import org.manalith.irc.ui.ApplicationWindow
import org.pircbotx.PircBotX
import org.pircbotx.exception.IrcException
import org.pircbotx.exception.NickAlreadyInUseException
import org.pircbotx.hooks.Listener
import org.pircbotx.hooks.ListenerAdapter
import org.pircbotx.hooks.events.JoinEvent
import org.pircbotx.hooks.events.TopicEvent
import org.pircbotx.hooks.events.UserListEvent

class Connection(server: Server, window: ApplicationWindow) {
	lazy val logger = Logger.getLogger(this.getClass().getName());
	private var bot: PircBotX = new PircBotX();
	addEventListener(new EventListener());

	def connect: Boolean = {
		if (bot.isConnected()) {
			bot.disconnect();
		}

		try {
			bot.setName(server.nickname);
			bot.connect(server.hostname, server.port, server.password);
			for (channel <- server.defaultChannels) {
				bot.joinChannel(channel);
			}

			return true;
		} catch {
			case e: NickAlreadyInUseException =>
				logger.error(e.getMessage(), e);
			case e: IOException =>
				logger.error(e.getMessage(), e);
			case e: IrcException =>
				logger.error(e.getMessage(), e);
		}

		return false;
	}

	def isConnected = bot.isConnected();

	def quitServer(reason: String) = {
		if (bot.isConnected()) {
			bot.quitServer(reason);
		}
	}

	def disconnect = {
		if (bot.isConnected()) {
			bot.disconnect();
		}
	}

	def addEventListener(listener: Listener[PircBotX]) = {
		bot.getListenerManager().addListener(listener);
	}

	def removeEventListener(listener: Listener[PircBotX]) = {
		bot.getListenerManager().removeListener(listener);
	}

	def getServer = server;

	def sendMessage(target: String, message: String) = {
		bot.sendMessage(target, message);
	}

	def nick = bot.getNick();

	private class EventListener extends ListenerAdapter[PircBotX] {
		@throws(classOf[Exception])
		override def onTopic(event: TopicEvent[PircBotX]) = {
			val view = window.getChannelView(event.getChannel()
				.getName());
			if (view != null) {
				Display.getDefault().asyncExec(new Runnable() {
					def run = {
						view.setTopic(event.getTopic());
					}
				});
			}
		}

		@throws(classOf[Exception])
		override def onUserList(event: UserListEvent[PircBotX]) = {
			val view = window.getChannelView(event.getChannel()
				.getName());
			if (view != null) {
				Display.getDefault().asyncExec(new Runnable() {
					def run = {
						view.updateUserList(event.getUsers());
					}
				});
			}
		}

		@throws(classOf[Exception])
		override def onJoin(event: JoinEvent[PircBotX]) = {
			val nick = event.getUser().getNick();

			if (nick.equals(nick)) {
				val view = window.getChannelView(event
					.getChannel().getName());

				if (view == null) {
					Display.getDefault().asyncExec(new Runnable() {
						def run = {
							window.createChannelTab(event.getChannel(),
								Connection.this);
						}
					});
				}
			}
		}
	}
}
