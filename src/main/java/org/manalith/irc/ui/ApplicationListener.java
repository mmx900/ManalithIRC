package org.manalith.irc.ui;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TabItem;
import org.manalith.irc.Application;
import org.manalith.irc.model.Connection;
import org.manalith.irc.model.Server;
import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.Listener;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.ActionEvent;
import org.pircbotx.hooks.events.ChannelInfoEvent;
import org.pircbotx.hooks.events.ConnectEvent;
import org.pircbotx.hooks.events.DisconnectEvent;
import org.pircbotx.hooks.events.FileTransferFinishedEvent;
import org.pircbotx.hooks.events.FingerEvent;
import org.pircbotx.hooks.events.HalfOpEvent;
import org.pircbotx.hooks.events.IncomingChatRequestEvent;
import org.pircbotx.hooks.events.IncomingFileTransferEvent;
import org.pircbotx.hooks.events.InviteEvent;
import org.pircbotx.hooks.events.JoinEvent;
import org.pircbotx.hooks.events.KickEvent;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.ModeEvent;
import org.pircbotx.hooks.events.MotdEvent;
import org.pircbotx.hooks.events.NickChangeEvent;
import org.pircbotx.hooks.events.NoticeEvent;
import org.pircbotx.hooks.events.OpEvent;
import org.pircbotx.hooks.events.OwnerEvent;
import org.pircbotx.hooks.events.PartEvent;
import org.pircbotx.hooks.events.PingEvent;
import org.pircbotx.hooks.events.PrivateMessageEvent;
import org.pircbotx.hooks.events.QuitEvent;
import org.pircbotx.hooks.events.RemoveChannelBanEvent;
import org.pircbotx.hooks.events.RemoveChannelKeyEvent;
import org.pircbotx.hooks.events.RemoveChannelLimitEvent;
import org.pircbotx.hooks.events.RemoveInviteOnlyEvent;
import org.pircbotx.hooks.events.RemoveModeratedEvent;
import org.pircbotx.hooks.events.RemoveNoExternalMessagesEvent;
import org.pircbotx.hooks.events.RemovePrivateEvent;
import org.pircbotx.hooks.events.RemoveSecretEvent;
import org.pircbotx.hooks.events.RemoveTopicProtectionEvent;
import org.pircbotx.hooks.events.ServerPingEvent;
import org.pircbotx.hooks.events.ServerResponseEvent;
import org.pircbotx.hooks.events.SetChannelBanEvent;
import org.pircbotx.hooks.events.SetChannelKeyEvent;
import org.pircbotx.hooks.events.SetChannelLimitEvent;
import org.pircbotx.hooks.events.SetInviteOnlyEvent;
import org.pircbotx.hooks.events.SetModeratedEvent;
import org.pircbotx.hooks.events.SetNoExternalMessagesEvent;
import org.pircbotx.hooks.events.SetPrivateEvent;
import org.pircbotx.hooks.events.SetSecretEvent;
import org.pircbotx.hooks.events.SetTopicProtectionEvent;
import org.pircbotx.hooks.events.SuperOpEvent;
import org.pircbotx.hooks.events.TimeEvent;
import org.pircbotx.hooks.events.TopicEvent;
import org.pircbotx.hooks.events.UnknownEvent;
import org.pircbotx.hooks.events.UserListEvent;
import org.pircbotx.hooks.events.UserModeEvent;
import org.pircbotx.hooks.events.VersionEvent;
import org.pircbotx.hooks.events.VoiceEvent;
import org.pircbotx.hooks.types.GenericCTCPCommand;
import org.pircbotx.hooks.types.GenericChannelModeEvent;
import org.pircbotx.hooks.types.GenericDCCEvent;
import org.pircbotx.hooks.types.GenericMessageEvent;
import org.pircbotx.hooks.types.GenericUserModeEvent;

public class ApplicationListener extends ListenerAdapter<PircBotX> implements
		Listener<PircBotX>, ActionListener {
	private ApplicationWindow window;
	private Application application;
	private List<Connection> connections = new ArrayList<Connection>();
	private ApplicationListener instance;

	public ApplicationListener(ApplicationWindow window, Application application) {
		this.window = window;
		this.application = application;
		this.instance = this;
	}

	public void onAction(Action action) {
		if (action.getCommand().equals(
				ApplicationWindow.EVENT_CONNECT_BUTTON_CLICKED)) {
			Configuration config = application.getConfiguration();
			Server server = new Server();
			server.setHostname(config.getString("server.host"));
			server.setPort(config.getInt("server.port"));
			server.setEncoding(config.getString("server.encoding"));
			server.setVerbose(config.getBoolean("server.verbose"));
			server.setNickname(config.getString("server.nickname"));
			server.setDefaultChannels(config.getList("server.defaultChannels"));

			TabItem serverTab = window.createServerTab(server.getHostname());
			ServerMessageView view = (ServerMessageView) serverTab.getControl();
			view.addActionListener(this);

			Connection connection = application.createConnection(server);
			connection.setServerView(view);
			connection.addEventListener(this);
			connections.add(connection);

			if (connection.connect()) {

			}
		} else if (action.getCommand().equals(
				ApplicationWindow.EVENT_WINDOW_DISPOSED)) {
			application.disconnectAllConnection();
		} else if (action.getCommand().equals(
				ChannelView.EVENT_MESSAGE_SUBMITTED)) {
			ChannelView view = (ChannelView) action.getSourceContainer();
			Connection conn = view.getConnection();
			String message = view.getMessageInput().getText();
			conn.sendMessage(view.getChannelName(), message);
			view.getMessageInput().setText("");
			view.printMessage(String.format("<%1s> %2s", conn.getNick(),
					message));
		}
	}

	private Connection getConnection(String hostName) {
		for (Connection conn : connections) {
			if (conn.getServer().getHostname().equals(hostName)) {
				return conn;
			}
		}

		return null;
	}

	public void onAction(ActionEvent event) throws Exception {
		Connection conn = getConnection(event.getBot().getServer());
		Channel channel = event.getChannel();
		conn.getChannelView(channel.getName()).printAsyncMessage(
				String.format("\t\t %1s %2s", event.getUser().getNick(),
						event.getMessage()));
	}

	public void onChannelInfo(ChannelInfoEvent<PircBotX> event)
			throws Exception {
		getConnection(event.getBot().getServer()).getServerView()
				.printAsyncMessage(event.getClass().getName());
	}

	public void onConnect(ConnectEvent<PircBotX> event) throws Exception {
		getConnection(event.getBot().getServer()).getServerView()
				.printAsyncMessage(event.getClass().getName());
	}

	public void onDisconnect(DisconnectEvent<PircBotX> event) throws Exception {
		getConnection(event.getBot().getServer()).getServerView()
				.printAsyncMessage(event.getClass().getName());
	}

	public void onFileTransferFinished(FileTransferFinishedEvent<PircBotX> event)
			throws Exception {
		getConnection(event.getBot().getServer()).getServerView()
				.printAsyncMessage(event.getClass().getName());
	}

	public void onFinger(FingerEvent<PircBotX> event) throws Exception {
		getConnection(event.getBot().getServer()).getServerView()
				.printAsyncMessage(event.getClass().getName());
	}

	public void onHalfOp(HalfOpEvent<PircBotX> event) throws Exception {
		getConnection(event.getBot().getServer()).getServerView()
				.printAsyncMessage(event.getClass().getName());
	}

	public void onIncomingChatRequest(IncomingChatRequestEvent<PircBotX> event)
			throws Exception {
		getConnection(event.getBot().getServer()).getServerView()
				.printAsyncMessage(event.getClass().getName());
	}

	public void onIncomingFileTransfer(IncomingFileTransferEvent<PircBotX> event)
			throws Exception {
		getConnection(event.getBot().getServer()).getServerView()
				.printAsyncMessage(event.getClass().getName());
	}

	public void onInvite(InviteEvent<PircBotX> event) throws Exception {
		getConnection(event.getBot().getServer()).getServerView()
				.printAsyncMessage(event.getClass().getName());
	}

	public void onJoin(final JoinEvent<PircBotX> event) throws Exception {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				Connection connection = getConnection(event.getBot()
						.getServer());
				String channelName = event.getChannel().getName();
				String nick = event.getUser().getNick();

				if (nick.equals(connection.getNick())) {
					TabItem channelTab = window.createChannelTab(channelName,
							channelName, connection);
					ChannelView view = (ChannelView) channelTab.getControl();
					view.addActionListener(instance);
					connection.getChannelViewList().add(view);
				} else {
					ChannelView view = connection.getChannelView(channelName);
					view.printMessage(String.format("%1s 님이 입장하셨습니다.", nick));
				}
			}
		});
	}

	public void onKick(KickEvent<PircBotX> event) throws Exception {
		getConnection(event.getBot().getServer()).getServerView()
				.printAsyncMessage(event.getClass().getName());
	}

	public void onMessage(MessageEvent<PircBotX> event) throws Exception {
		Connection conn = getConnection(event.getBot().getServer());
		Channel channel = event.getChannel();
		if (channel == null) {
			conn.getServerView().printAsyncMessage(
					String.format("<%1s> %2s", event.getUser().getNick(),
							event.getMessage()));
		} else {
			conn.getChannelView(channel.getName()).printAsyncMessage(
					String.format("<%1s> %2s", event.getUser().getNick(),
							event.getMessage()));
		}
	}

	public void onMode(ModeEvent<PircBotX> event) throws Exception {
		getConnection(event.getBot().getServer()).getServerView()
				.printAsyncMessage(event.getClass().getName());
	}

	public void onMotd(MotdEvent<PircBotX> event) throws Exception {
		getConnection(event.getBot().getServer()).getServerView()
				.printAsyncMessage(event.getClass().getName());
	}

	public void onNickChange(NickChangeEvent<PircBotX> event) throws Exception {
		getConnection(event.getBot().getServer()).getServerView()
				.printAsyncMessage(event.getClass().getName());
	}

	public void onNotice(NoticeEvent<PircBotX> event) throws Exception {
		getConnection(event.getBot().getServer()).getServerView()
				.printAsyncMessage(event.getMessage());
	}

	public void onOp(OpEvent<PircBotX> event) throws Exception {
		getConnection(event.getBot().getServer()).getServerView()
				.printAsyncMessage(event.getClass().getName());
	}

	public void onOwner(OwnerEvent<PircBotX> event) throws Exception {
		getConnection(event.getBot().getServer()).getServerView()
				.printAsyncMessage(event.getClass().getName());
	}

	public void onPart(final PartEvent<PircBotX> event) throws Exception {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				Connection connection = getConnection(event.getBot()
						.getServer());
				String channelName = event.getChannel().getName();
				String nick = event.getUser().getNick();

				if (nick.equals(connection.getNick())) {
					// TODO
					/*
					 * TabItem channelTab = window.createChannelTab(channelName,
					 * channelName, connection); ChannelView view =
					 * (ChannelView) channelTab.getControl();
					 * view.addActionListener(instance);
					 * connection.getChannelViewList().add(view);
					 */
				} else {
					ChannelView view = connection.getChannelView(channelName);
					view.printMessage(String.format("%1s 님이 퇴장하셨습니다. (%2s)",
							nick, event.getReason()));
				}
			}
		});
	}

	public void onPing(PingEvent<PircBotX> event) throws Exception {
		getConnection(event.getBot().getServer()).getServerView()
				.printAsyncMessage(event.getClass().getName());
	}

	public void onPrivateMessage(PrivateMessageEvent<PircBotX> event)
			throws Exception {
		getConnection(event.getBot().getServer()).getServerView()
				.printAsyncMessage(event.getClass().getName());
	}

	public void onQuit(final QuitEvent<PircBotX> event) throws Exception {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				// TODO
				/*
				 * Connection connection = getConnection(event.getBot()
				 * .getServer()); String channelName =
				 * event.getChannel().getName(); String nick =
				 * event.getUser().getNick();
				 * 
				 * if (nick.equals(connection.getNick())) { // TODO } else {
				 * ChannelView view = connection.getChannelView(channelName);
				 * view.printMessage(String.format("%1s 님이 종료하셨습니다. (%2s)",
				 * nick, event.getReason())); }
				 */
			}
		});
	}

	public void onRemoveChannelBan(RemoveChannelBanEvent<PircBotX> event)
			throws Exception {
		getConnection(event.getBot().getServer()).getServerView()
				.printAsyncMessage(event.getClass().getName());
	}

	public void onRemoveChannelKey(RemoveChannelKeyEvent<PircBotX> event)
			throws Exception {
		getConnection(event.getBot().getServer()).getServerView()
				.printAsyncMessage(event.getClass().getName());
	}

	public void onRemoveChannelLimit(RemoveChannelLimitEvent<PircBotX> event)
			throws Exception {
		getConnection(event.getBot().getServer()).getServerView()
				.printAsyncMessage(event.getClass().getName());
	}

	public void onRemoveInviteOnly(RemoveInviteOnlyEvent<PircBotX> event)
			throws Exception {
		getConnection(event.getBot().getServer()).getServerView()
				.printAsyncMessage(event.getClass().getName());
	}

	public void onRemoveModerated(RemoveModeratedEvent<PircBotX> event)
			throws Exception {
		getConnection(event.getBot().getServer()).getServerView()
				.printAsyncMessage(event.getClass().getName());
	}

	public void onRemoveNoExternalMessages(
			RemoveNoExternalMessagesEvent<PircBotX> event) throws Exception {
		getConnection(event.getBot().getServer()).getServerView()
				.printAsyncMessage(event.getClass().getName());
	}

	public void onRemovePrivate(RemovePrivateEvent<PircBotX> event)
			throws Exception {
		getConnection(event.getBot().getServer()).getServerView()
				.printAsyncMessage(event.getClass().getName());
	}

	public void onRemoveSecret(RemoveSecretEvent<PircBotX> event)
			throws Exception {
		getConnection(event.getBot().getServer()).getServerView()
				.printAsyncMessage(event.getClass().getName());
	}

	public void onRemoveTopicProtection(
			RemoveTopicProtectionEvent<PircBotX> event) throws Exception {
		getConnection(event.getBot().getServer()).getServerView()
				.printAsyncMessage(event.getClass().getName());
	}

	public void onServerPing(ServerPingEvent<PircBotX> event) throws Exception {
		getConnection(event.getBot().getServer()).getServerView()
				.printAsyncMessage(event.getResponse());
	}

	public void onServerResponse(ServerResponseEvent<PircBotX> event)
			throws Exception {
		getConnection(event.getBot().getServer()).getServerView()
				.printAsyncMessage(event.getResponse());
	}

	public void onSetChannelBan(SetChannelBanEvent<PircBotX> event)
			throws Exception {
		getConnection(event.getBot().getServer()).getServerView()
				.printAsyncMessage(event.getClass().getName());
	}

	public void onSetChannelKey(SetChannelKeyEvent<PircBotX> event)
			throws Exception {
		getConnection(event.getBot().getServer()).getServerView()
				.printAsyncMessage(event.getClass().getName());
	}

	public void onSetChannelLimit(SetChannelLimitEvent<PircBotX> event)
			throws Exception {
		getConnection(event.getBot().getServer()).getServerView()
				.printAsyncMessage(event.getClass().getName());
	}

	public void onSetInviteOnly(SetInviteOnlyEvent<PircBotX> event)
			throws Exception {
		getConnection(event.getBot().getServer()).getServerView()
				.printAsyncMessage(event.getClass().getName());
	}

	public void onSetModerated(SetModeratedEvent<PircBotX> event)
			throws Exception {
		getConnection(event.getBot().getServer()).getServerView()
				.printAsyncMessage(event.getClass().getName());
	}

	public void onSetNoExternalMessages(
			SetNoExternalMessagesEvent<PircBotX> event) throws Exception {
		getConnection(event.getBot().getServer()).getServerView()
				.printAsyncMessage(event.getClass().getName());
	}

	public void onSetPrivate(SetPrivateEvent<PircBotX> event) throws Exception {
		getConnection(event.getBot().getServer()).getServerView()
				.printAsyncMessage(event.getClass().getName());
	}

	public void onSetSecret(SetSecretEvent<PircBotX> event) throws Exception {
		getConnection(event.getBot().getServer()).getServerView()
				.printAsyncMessage(event.getClass().getName());
	}

	public void onSetTopicProtection(SetTopicProtectionEvent<PircBotX> event)
			throws Exception {
		getConnection(event.getBot().getServer()).getServerView()
				.printAsyncMessage(event.getClass().getName());
	}

	public void onSuperOp(SuperOpEvent<PircBotX> event) throws Exception {
		getConnection(event.getBot().getServer()).getServerView()
				.printAsyncMessage(event.getClass().getName());
	}

	public void onTime(TimeEvent<PircBotX> event) throws Exception {
		getConnection(event.getBot().getServer()).getServerView()
				.printAsyncMessage(event.getClass().getName());
	}

	public void onTopic(final TopicEvent<PircBotX> event) throws Exception {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				Connection conn = getConnection(event.getBot().getServer());
				String channelName = event.getChannel().getName();
				conn.getChannelView(channelName).setTopic(event.getTopic());
			}
		});
	}

	public void onUnknown(UnknownEvent<PircBotX> event) throws Exception {
		getConnection(event.getBot().getServer()).getServerView()
				.printAsyncMessage(event.getClass().getName());
	}

	public void onUserList(final UserListEvent<PircBotX> event)
			throws Exception {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				Connection conn = getConnection(event.getBot().getServer());
				String channelName = event.getChannel().getName();
				conn.getChannelView(channelName).updateUserList(
						event.getUsers());
			}
		});
	}

	public void onUserMode(UserModeEvent<PircBotX> event) throws Exception {
		getConnection(event.getBot().getServer()).getServerView()
				.printAsyncMessage(event.getClass().getName());
	}

	public void onVersion(VersionEvent<PircBotX> event) throws Exception {
		getConnection(event.getBot().getServer()).getServerView()
				.printAsyncMessage(event.getClass().getName());
	}

	public void onVoice(VoiceEvent<PircBotX> event) throws Exception {
		getConnection(event.getBot().getServer()).getServerView()
				.printAsyncMessage(event.getClass().getName());
	}

	public void onGenericCTCPCommand(GenericCTCPCommand<PircBotX> event)
			throws Exception {
		getConnection(event.getBot().getServer()).getServerView()
				.printAsyncMessage(event.getClass().getName());
	}

	public void onGenericUserMode(GenericUserModeEvent<PircBotX> event)
			throws Exception {
		getConnection(event.getBot().getServer()).getServerView()
				.printAsyncMessage(event.getClass().getName());
	}

	public void onGenericChannelMode(GenericChannelModeEvent<PircBotX> event)
			throws Exception {
		getConnection(event.getBot().getServer()).getServerView()
				.printAsyncMessage(event.getClass().getName());
	}

	public void onGenericDCC(GenericDCCEvent<PircBotX> event) throws Exception {
		getConnection(event.getBot().getServer()).getServerView()
				.printAsyncMessage(event.getClass().getName());
	}

	public void onGenericMessage(GenericMessageEvent<PircBotX> event)
			throws Exception {
		getConnection(event.getBot().getServer()).getServerView()
				.printAsyncMessage(event.getMessage());
	}

}
