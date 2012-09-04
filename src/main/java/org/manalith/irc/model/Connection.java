package org.manalith.irc.model;

import java.io.IOException;

import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.widgets.Display;
import org.manalith.irc.ui.ApplicationWindow;
import org.manalith.irc.ui.ChannelView;
import org.pircbotx.PircBotX;
import org.pircbotx.exception.IrcException;
import org.pircbotx.exception.NickAlreadyInUseException;
import org.pircbotx.hooks.Listener;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.JoinEvent;
import org.pircbotx.hooks.events.TopicEvent;
import org.pircbotx.hooks.events.UserListEvent;

public class Connection {
	private PircBotX bot;
	private Server server;
	private ApplicationWindow window;

	public Connection(Server server, ApplicationWindow window) {
		this.server = server;
		this.window = window;
		bot = new PircBotX();
		addEventListener(new EventListener());
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

	public boolean isConnected() {
		return bot.isConnected();
	}

	public void quitServer(String reason) {
		if (bot.isConnected()) {
			bot.quitServer(reason);
		}
	}

	public void disconnect() {
		if (bot.isConnected()) {
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

	public void sendMessage(String target, String message) {
		bot.sendMessage(target, message);
	}

	public String getNick() {
		return bot.getNick();
	}

	private class EventListener extends ListenerAdapter<PircBotX> {
		public void onTopic(final TopicEvent<PircBotX> event) throws Exception {
			final ChannelView view = window.getChannelView(event.getChannel()
					.getName());
			if (view != null) {
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						view.setTopic(event.getTopic());
					}
				});
			}
		}

		public void onUserList(final UserListEvent<PircBotX> event)
				throws Exception {
			final ChannelView view = window.getChannelView(event.getChannel()
					.getName());
			if (view != null) {
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						view.updateUserList(event.getUsers());
					}
				});
			}
		}

		public void onJoin(final JoinEvent<PircBotX> event) throws Exception {
			String nick = event.getUser().getNick();

			if (nick.equals(getNick())) {
				final ChannelView view = window.getChannelView(event
						.getChannel().getName());

				if (view == null) {
					Display.getDefault().asyncExec(new Runnable() {
						public void run() {
							window.createChannelTab(
									event.getChannel(), Connection.this);
						}
					});
				}
			}
		}
	}
}
