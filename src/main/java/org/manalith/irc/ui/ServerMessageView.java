package org.manalith.irc.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.manalith.irc.model.Connection;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.ChannelInfoEvent;
import org.pircbotx.hooks.events.ConnectEvent;
import org.pircbotx.hooks.events.DisconnectEvent;
import org.pircbotx.hooks.events.FileTransferFinishedEvent;
import org.pircbotx.hooks.events.FingerEvent;
import org.pircbotx.hooks.events.HalfOpEvent;
import org.pircbotx.hooks.events.IncomingChatRequestEvent;
import org.pircbotx.hooks.events.IncomingFileTransferEvent;
import org.pircbotx.hooks.events.InviteEvent;
import org.pircbotx.hooks.events.KickEvent;
import org.pircbotx.hooks.events.ModeEvent;
import org.pircbotx.hooks.events.MotdEvent;
import org.pircbotx.hooks.events.NickChangeEvent;
import org.pircbotx.hooks.events.NoticeEvent;
import org.pircbotx.hooks.events.OpEvent;
import org.pircbotx.hooks.events.OwnerEvent;
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
import org.pircbotx.hooks.events.UnknownEvent;
import org.pircbotx.hooks.events.UserModeEvent;
import org.pircbotx.hooks.events.VersionEvent;
import org.pircbotx.hooks.events.VoiceEvent;
import org.pircbotx.hooks.types.GenericCTCPCommand;
import org.pircbotx.hooks.types.GenericChannelModeEvent;
import org.pircbotx.hooks.types.GenericDCCEvent;
import org.pircbotx.hooks.types.GenericMessageEvent;
import org.pircbotx.hooks.types.GenericUserModeEvent;

import swing2swt.layout.BorderLayout;

public class ServerMessageView extends Composite implements IrcTab {
	private List<ActionListener> actionListeners = new ArrayList<ActionListener>();
	private StyledText messageOutput;
	private Connection connection;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public ServerMessageView(Composite parent, int style, Connection connection) {
		super(parent, style);
		setLayout(new BorderLayout(0, 0));

		messageOutput = new StyledText(this, SWT.BORDER | SWT.H_SCROLL
				| SWT.V_SCROLL);
		messageOutput.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				messageOutput.setTopIndex(messageOutput.getLineCount() - 1);
			}
		});

		messageOutput.setLayoutData(BorderLayout.CENTER);

		this.connection = connection;
		connection.addEventListener(new ServerEventDispatcher());
		addActionListener(new ActionAdapter());
	}

	public StyledText getMessageOutput() {
		return messageOutput;
	}

	public void printMessage(String message) {
		messageOutput.append(message + "\n");
	}

	public void printAsyncMessage(final String message) {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				messageOutput.append(message + "\n");
			}
		});
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

	private class ActionAdapter implements ActionListener {
		public void onAction(Action action) {
			switch (action.getCommand()) {

			}
		}
	}

	private class ServerEventDispatcher extends ListenerAdapter<PircBotX> {
		public void onChannelInfo(ChannelInfoEvent<PircBotX> event)
				throws Exception {
			printAsyncMessage(event.getClass().getName());
		}

		public void onConnect(ConnectEvent<PircBotX> event) throws Exception {
			printAsyncMessage(event.getClass().getName());
		}

		public void onDisconnect(DisconnectEvent<PircBotX> event)
				throws Exception {
			printAsyncMessage(event.getClass().getName());
		}

		public void onFileTransferFinished(
				FileTransferFinishedEvent<PircBotX> event) throws Exception {
			printAsyncMessage(event.getClass().getName());
		}

		public void onFinger(FingerEvent<PircBotX> event) throws Exception {
			printAsyncMessage(event.getClass().getName());
		}

		public void onHalfOp(HalfOpEvent<PircBotX> event) throws Exception {
			printAsyncMessage(event.getClass().getName());
		}

		public void onIncomingChatRequest(
				IncomingChatRequestEvent<PircBotX> event) throws Exception {
			printAsyncMessage(event.getClass().getName());
		}

		public void onIncomingFileTransfer(
				IncomingFileTransferEvent<PircBotX> event) throws Exception {
			printAsyncMessage(event.getClass().getName());
		}

		public void onInvite(InviteEvent<PircBotX> event) throws Exception {
			printAsyncMessage(event.getClass().getName());
		}

		public void onKick(KickEvent<PircBotX> event) throws Exception {
			printAsyncMessage(event.getClass().getName());
		}

		public void onMode(ModeEvent<PircBotX> event) throws Exception {
			printAsyncMessage(event.getClass().getName());
		}

		public void onMotd(MotdEvent<PircBotX> event) throws Exception {
			printAsyncMessage(event.getClass().getName());
		}

		public void onNickChange(NickChangeEvent<PircBotX> event)
				throws Exception {
			printAsyncMessage(event.getClass().getName());
		}

		public void onNotice(NoticeEvent<PircBotX> event) throws Exception {
			printAsyncMessage(event.getMessage());
		}

		public void onOp(OpEvent<PircBotX> event) throws Exception {
			printAsyncMessage(event.getClass().getName());
		}

		public void onOwner(OwnerEvent<PircBotX> event) throws Exception {
			printAsyncMessage(event.getClass().getName());
		}

		public void onPing(PingEvent<PircBotX> event) throws Exception {
			printAsyncMessage(event.getClass().getName());
		}

		public void onPrivateMessage(PrivateMessageEvent<PircBotX> event)
				throws Exception {
			printAsyncMessage(event.getClass().getName());
		}

		public void onRemoveChannelBan(RemoveChannelBanEvent<PircBotX> event)
				throws Exception {
			printAsyncMessage(event.getClass().getName());
		}

		public void onRemoveChannelKey(RemoveChannelKeyEvent<PircBotX> event)
				throws Exception {
			printAsyncMessage(event.getClass().getName());
		}

		public void onRemoveChannelLimit(RemoveChannelLimitEvent<PircBotX> event)
				throws Exception {
			printAsyncMessage(event.getClass().getName());
		}

		public void onRemoveInviteOnly(RemoveInviteOnlyEvent<PircBotX> event)
				throws Exception {
			printAsyncMessage(event.getClass().getName());
		}

		public void onRemoveModerated(RemoveModeratedEvent<PircBotX> event)
				throws Exception {
			printAsyncMessage(event.getClass().getName());
		}

		public void onRemoveNoExternalMessages(
				RemoveNoExternalMessagesEvent<PircBotX> event) throws Exception {
			printAsyncMessage(event.getClass().getName());
		}

		public void onRemovePrivate(RemovePrivateEvent<PircBotX> event)
				throws Exception {
			printAsyncMessage(event.getClass().getName());
		}

		public void onRemoveSecret(RemoveSecretEvent<PircBotX> event)
				throws Exception {
			printAsyncMessage(event.getClass().getName());
		}

		public void onRemoveTopicProtection(
				RemoveTopicProtectionEvent<PircBotX> event) throws Exception {
			printAsyncMessage(event.getClass().getName());
		}

		public void onServerPing(ServerPingEvent<PircBotX> event)
				throws Exception {
			printAsyncMessage(event.getResponse());
		}

		public void onServerResponse(ServerResponseEvent<PircBotX> event)
				throws Exception {
			printAsyncMessage(event.getResponse());
		}

		public void onSetChannelBan(SetChannelBanEvent<PircBotX> event)
				throws Exception {
			printAsyncMessage(event.getClass().getName());
		}

		public void onSetChannelKey(SetChannelKeyEvent<PircBotX> event)
				throws Exception {
			printAsyncMessage(event.getClass().getName());
		}

		public void onSetChannelLimit(SetChannelLimitEvent<PircBotX> event)
				throws Exception {
			printAsyncMessage(event.getClass().getName());
		}

		public void onSetInviteOnly(SetInviteOnlyEvent<PircBotX> event)
				throws Exception {
			printAsyncMessage(event.getClass().getName());
		}

		public void onSetModerated(SetModeratedEvent<PircBotX> event)
				throws Exception {
			printAsyncMessage(event.getClass().getName());
		}

		public void onSetNoExternalMessages(
				SetNoExternalMessagesEvent<PircBotX> event) throws Exception {
			printAsyncMessage(event.getClass().getName());
		}

		public void onSetPrivate(SetPrivateEvent<PircBotX> event)
				throws Exception {
			printAsyncMessage(event.getClass().getName());
		}

		public void onSetSecret(SetSecretEvent<PircBotX> event)
				throws Exception {
			printAsyncMessage(event.getClass().getName());
		}

		public void onSetTopicProtection(SetTopicProtectionEvent<PircBotX> event)
				throws Exception {
			printAsyncMessage(event.getClass().getName());
		}

		public void onSuperOp(SuperOpEvent<PircBotX> event) throws Exception {
			printAsyncMessage(event.getClass().getName());
		}

		public void onTime(TimeEvent<PircBotX> event) throws Exception {
			printAsyncMessage(event.getClass().getName());
		}

		public void onUnknown(UnknownEvent<PircBotX> event) throws Exception {
			printAsyncMessage(event.getClass().getName());
		}

		public void onUserMode(UserModeEvent<PircBotX> event) throws Exception {
			printAsyncMessage(event.getClass().getName());
		}

		public void onVersion(VersionEvent<PircBotX> event) throws Exception {
			printAsyncMessage(event.getClass().getName());
		}

		public void onVoice(VoiceEvent<PircBotX> event) throws Exception {
			printAsyncMessage(event.getClass().getName());
		}

		public void onGenericCTCPCommand(GenericCTCPCommand<PircBotX> event)
				throws Exception {
			printAsyncMessage(event.getClass().getName());
		}

		public void onGenericUserMode(GenericUserModeEvent<PircBotX> event)
				throws Exception {
			printAsyncMessage(event.getClass().getName());
		}

		public void onGenericChannelMode(GenericChannelModeEvent<PircBotX> event)
				throws Exception {
			printAsyncMessage(event.getClass().getName());
		}

		public void onGenericDCC(GenericDCCEvent<PircBotX> event)
				throws Exception {
			printAsyncMessage(event.getClass().getName());
		}

		public void onGenericMessage(GenericMessageEvent<PircBotX> event)
				throws Exception {
			printAsyncMessage(event.getMessage());
		}

		public void onQuit(final QuitEvent<PircBotX> event) throws Exception {
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					// TODO 셀프 메시지일 경우 닫기
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
