package org.manalith.irc.model;

import java.io.IOException
import scala.collection.JavaConversions.asScalaBuffer
import org.manalith.irc.helper.LogHelper
import org.pircbotx.PircBotX
import org.pircbotx.exception.IrcException
import org.pircbotx.exception.NickAlreadyInUseException
import org.pircbotx.hooks.Listener

class Connection(val server: Server, bot: PircBotX) extends LogHelper {

	def connect: Boolean = {
		if (bot.isConnected()) {
			bot.disconnect();
		}

		try {
			bot.setName(server.nickname);
			bot.setLogin(server.login);
			bot.connect(server.hostname, server.port, server.password);
			for (channel <- server.defaultChannels) {
				if (channel.password == null) {
					bot.joinChannel(channel.name);
				} else {
					bot.joinChannel(channel.name, channel.password);
				}
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

	def partChannel(channel: org.pircbotx.Channel) = {
		bot.partChannel(channel)
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

	def sendMessage(target: String, message: String) = {
		bot.sendMessage(target, message);
	}

	def nick = bot.getNick();
}
