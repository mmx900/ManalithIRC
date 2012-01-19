package org.manalith.irc.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.manalith.irc.ui.ChannelView;
import org.manalith.irc.ui.ServerMessageView;
import org.pircbotx.PircBotX;
import org.pircbotx.exception.IrcException;
import org.pircbotx.exception.NickAlreadyInUseException;
import org.pircbotx.hooks.Listener;

public class Connection {
	private PircBotX bot;
	private Server server;
	private ServerMessageView serverView;
	private List<ChannelView> channelViewList = new ArrayList<ChannelView>();

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

	public void addEventListener(Listener<PircBotX> listener) {
		bot.getListenerManager().addListener(listener);
	}

	public void removeEventListener(Listener<PircBotX> listener) {
		bot.getListenerManager().removeListener(listener);
	}

	public Server getServer() {
		return server;
	}

	public ServerMessageView getServerView() {
		return serverView;
	}

	public void setServerView(ServerMessageView serverView) {
		this.serverView = serverView;
	}

	public List<ChannelView> getChannelViewList() {
		return channelViewList;
	}

	public void setChannelViewList(List<ChannelView> channelViewList) {
		this.channelViewList = channelViewList;
	}

	public ChannelView getChannelView(String channelName) {
		for (ChannelView view : channelViewList) {
			if (view.getChannelName().equals(channelName)) {
				return view;
			}
		}

		return null;
	}

	public void sendMessage(String target, String message) {
		bot.sendMessage(target, message);
	}

	public String getNick() {
		return bot.getNick();
	}
}
