package org.manalith.irc.model;

import java.io.IOException;

import org.pircbotx.PircBotX;
import org.pircbotx.exception.IrcException;
import org.pircbotx.exception.NickAlreadyInUseException;
import org.pircbotx.hooks.Listener;

public class Connection {
	private PircBotX bot;
	private Server server;

	public Connection(Server server) {
		this.server = server;
		bot = new PircBotX();
	}

	public boolean connect() {
		if (bot.isConnected()) {
			bot.disconnect();
		}

		try {
			bot.setName(server.getNickname());
			bot.connect(server.getHostname(), server.getPort(),
					server.getPassword());
			for (String channel : server.getDefaultChannels()) {
				bot.joinChannel(channel);
			}

			return true;
		} catch (NickAlreadyInUseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (IrcException e) {
			e.printStackTrace();
		}

		return false;
	}

	public void quitServer(String reason) {
		if (bot != null && bot.isConnected()) {
			bot.quitServer(reason);
		}
	}

	public void disconnect() {
		if (bot != null && bot.isConnected()) {
			bot.disconnect();
		}
	}

	public void addEventListener(Listener listener) {
		bot.getListenerManager().addListener(listener);
	}

	public void removeEventListener(Listener listener) {
		bot.getListenerManager().removeListener(listener);
	}
}
