package org.manalith.irc.ui;

import java.util.ArrayList
import java.util.List
import org.eclipse.swt.SWT
import org.eclipse.swt.custom.StyledText
import org.eclipse.swt.events.ModifyEvent
import org.eclipse.swt.events.ModifyListener
import org.eclipse.swt.widgets.Composite
import org.eclipse.swt.widgets.Display
import org.manalith.irc.model.Connection
import org.pircbotx.PircBotX
import org.pircbotx.hooks.ListenerAdapter
import org.pircbotx.hooks.events.ChannelInfoEvent
import org.pircbotx.hooks.events.ConnectEvent
import org.pircbotx.hooks.events.DisconnectEvent
import org.pircbotx.hooks.events.FileTransferFinishedEvent
import org.pircbotx.hooks.events.FingerEvent
import org.pircbotx.hooks.events.HalfOpEvent
import org.pircbotx.hooks.events.IncomingChatRequestEvent
import org.pircbotx.hooks.events.IncomingFileTransferEvent
import org.pircbotx.hooks.events.InviteEvent
import org.pircbotx.hooks.events.KickEvent
import org.pircbotx.hooks.events.ModeEvent
import org.pircbotx.hooks.events.MotdEvent
import org.pircbotx.hooks.events.NickChangeEvent
import org.pircbotx.hooks.events.NoticeEvent
import org.pircbotx.hooks.events.OpEvent
import org.pircbotx.hooks.events.OwnerEvent
import org.pircbotx.hooks.events.PingEvent
import org.pircbotx.hooks.events.PrivateMessageEvent
import org.pircbotx.hooks.events.QuitEvent
import org.pircbotx.hooks.events.RemoveChannelBanEvent
import org.pircbotx.hooks.events.RemoveChannelKeyEvent
import org.pircbotx.hooks.events.RemoveChannelLimitEvent
import org.pircbotx.hooks.events.RemoveInviteOnlyEvent
import org.pircbotx.hooks.events.RemoveModeratedEvent
import org.pircbotx.hooks.events.RemoveNoExternalMessagesEvent
import org.pircbotx.hooks.events.RemovePrivateEvent
import org.pircbotx.hooks.events.RemoveSecretEvent
import org.pircbotx.hooks.events.RemoveTopicProtectionEvent
import org.pircbotx.hooks.events.ServerPingEvent
import org.pircbotx.hooks.events.ServerResponseEvent
import org.pircbotx.hooks.events.SetChannelBanEvent
import org.pircbotx.hooks.events.SetChannelKeyEvent
import org.pircbotx.hooks.events.SetChannelLimitEvent
import org.pircbotx.hooks.events.SetInviteOnlyEvent
import org.pircbotx.hooks.events.SetModeratedEvent
import org.pircbotx.hooks.events.SetNoExternalMessagesEvent
import org.pircbotx.hooks.events.SetPrivateEvent
import org.pircbotx.hooks.events.SetSecretEvent
import org.pircbotx.hooks.events.SetTopicProtectionEvent
import org.pircbotx.hooks.events.SuperOpEvent
import org.pircbotx.hooks.events.TimeEvent
import org.pircbotx.hooks.events.UnknownEvent
import org.pircbotx.hooks.events.UserModeEvent
import org.pircbotx.hooks.events.VersionEvent
import org.pircbotx.hooks.events.VoiceEvent
import org.pircbotx.hooks.types.GenericCTCPCommand
import org.pircbotx.hooks.types.GenericChannelModeEvent
import org.pircbotx.hooks.types.GenericDCCEvent
import org.pircbotx.hooks.types.GenericMessageEvent
import org.pircbotx.hooks.types.GenericUserModeEvent
import swing2swt.layout.BorderLayout;

class ServerMessageView(parent: Composite, style: Int, private val connection: Connection) extends Composite(parent, style) with IrcTab {
	private val actionListeners: List[ActionListener] = new ArrayList[ActionListener]();

	setLayout(new BorderLayout(0, 0));

	val messageOutput: StyledText = new StyledText(this, SWT.BORDER | SWT.H_SCROLL
		| SWT.V_SCROLL); ;

	messageOutput.addModifyListener(new ModifyListener() {
		def modifyText(e: ModifyEvent) = {
			messageOutput.setTopIndex(messageOutput.getLineCount() - 1);
		}
	});
	messageOutput.setLayoutData(BorderLayout.CENTER);

	connection.addEventListener(new ServerEventDispatcher());
	addActionListener(new ActionAdapter());

	def printMessage(message: String) = {
		messageOutput.append(message + "\n");
	}

	def printAsyncMessage(message: String) = {
		Display.getDefault().asyncExec(new Runnable() {
			def run = {
				messageOutput.append(message + "\n");
			}
		});
	}

	override def checkSubclass = {
		// Disable the check that prevents subclassing of SWT components
	}

	def addActionListener(listener: ActionListener) = {
		actionListeners.add(listener);
	}

	def removeActionListener(listener: ActionListener) = {
		actionListeners.remove(listener);
	}

	private class ActionAdapter extends ActionListener {
		def onAction(action: Action) = {
			//			switch (action.command()) {
			//
			//			}
		}
	}

	private class ServerEventDispatcher extends ListenerAdapter[PircBotX] {
		@throws(classOf[Exception])
		override def onChannelInfo(event: ChannelInfoEvent[PircBotX]) {
			printAsyncMessage(event.getClass().getName());
		}

		@throws(classOf[Exception])
		override def onConnect(event: ConnectEvent[PircBotX]) {
			printAsyncMessage(event.getClass().getName());
		}

		@throws(classOf[Exception])
		override def onDisconnect(event: DisconnectEvent[PircBotX]) {
			printAsyncMessage(event.getClass().getName());
		}

		@throws(classOf[Exception])
		override def onFileTransferFinished(event: FileTransferFinishedEvent[PircBotX]) {
			printAsyncMessage(event.getClass().getName());
		}

		@throws(classOf[Exception])
		override def onFinger(event: FingerEvent[PircBotX]) {
			printAsyncMessage(event.getClass().getName());
		}

		@throws(classOf[Exception])
		override def onHalfOp(event: HalfOpEvent[PircBotX]) {
			printAsyncMessage(event.getClass().getName());
		}

		@throws(classOf[Exception])
		override def onIncomingChatRequest(event: IncomingChatRequestEvent[PircBotX]) {
			printAsyncMessage(event.getClass().getName());
		}

		@throws(classOf[Exception])
		override def onIncomingFileTransfer(
			event: IncomingFileTransferEvent[PircBotX]) {
			printAsyncMessage(event.getClass().getName());
		}

		@throws(classOf[Exception])
		override def onInvite(event: InviteEvent[PircBotX]) {
			printAsyncMessage(event.getClass().getName());
		}

		@throws(classOf[Exception])
		override def onKick(event: KickEvent[PircBotX]) {
			printAsyncMessage(event.getClass().getName());
		}

		@throws(classOf[Exception])
		override def onMode(event: ModeEvent[PircBotX]) {
			printAsyncMessage(event.getClass().getName());
		}

		@throws(classOf[Exception])
		override def onMotd(event: MotdEvent[PircBotX]) {
			printAsyncMessage(event.getClass().getName());
		}

		@throws(classOf[Exception])
		override def onNickChange(event: NickChangeEvent[PircBotX]) {
			printAsyncMessage(event.getClass().getName());
		}

		@throws(classOf[Exception])
		override def onNotice(event: NoticeEvent[PircBotX]) {
			printAsyncMessage(event.getMessage());
		}

		@throws(classOf[Exception])
		override def onOp(event: OpEvent[PircBotX]) {
			printAsyncMessage(event.getClass().getName());
		}

		@throws(classOf[Exception])
		override def onOwner(event: OwnerEvent[PircBotX]) {
			printAsyncMessage(event.getClass().getName());
		}

		@throws(classOf[Exception])
		override def onPing(event: PingEvent[PircBotX]) {
			printAsyncMessage(event.getClass().getName());
		}

		@throws(classOf[Exception])
		override def onPrivateMessage(event: PrivateMessageEvent[PircBotX]) {
			printAsyncMessage(event.getClass().getName());
		}

		@throws(classOf[Exception])
		override def onRemoveChannelBan(event: RemoveChannelBanEvent[PircBotX]) {
			printAsyncMessage(event.getClass().getName());
		}

		@throws(classOf[Exception])
		override def onRemoveChannelKey(event: RemoveChannelKeyEvent[PircBotX]) {
			printAsyncMessage(event.getClass().getName());
		}

		@throws(classOf[Exception])
		override def onRemoveChannelLimit(event: RemoveChannelLimitEvent[PircBotX]) {
			printAsyncMessage(event.getClass().getName());
		}

		@throws(classOf[Exception])
		override def onRemoveInviteOnly(event: RemoveInviteOnlyEvent[PircBotX]) {
			printAsyncMessage(event.getClass().getName());
		}

		@throws(classOf[Exception])
		override def onRemoveModerated(event: RemoveModeratedEvent[PircBotX]) {
			printAsyncMessage(event.getClass().getName());
		}

		@throws(classOf[Exception])
		override def onRemoveNoExternalMessages(
			event: RemoveNoExternalMessagesEvent[PircBotX]) {
			printAsyncMessage(event.getClass().getName());
		}

		@throws(classOf[Exception])
		override def onRemovePrivate(event: RemovePrivateEvent[PircBotX]) {
			printAsyncMessage(event.getClass().getName());
		}

		@throws(classOf[Exception])
		override def onRemoveSecret(event: RemoveSecretEvent[PircBotX]) {
			printAsyncMessage(event.getClass().getName());
		}

		@throws(classOf[Exception])
		override def onRemoveTopicProtection(
			event: RemoveTopicProtectionEvent[PircBotX]) {
			printAsyncMessage(event.getClass().getName());
		}

		@throws(classOf[Exception])
		override def onServerPing(event: ServerPingEvent[PircBotX]) {
			printAsyncMessage(event.getResponse());
		}

		@throws(classOf[Exception])
		override def onServerResponse(event: ServerResponseEvent[PircBotX]) {
			printAsyncMessage(event.getResponse());
		}

		@throws(classOf[Exception])
		override def onSetChannelBan(event: SetChannelBanEvent[PircBotX]) {
			printAsyncMessage(event.getClass().getName());
		}

		@throws(classOf[Exception])
		override def onSetChannelKey(event: SetChannelKeyEvent[PircBotX]) {
			printAsyncMessage(event.getClass().getName());
		}

		@throws(classOf[Exception])
		override def onSetChannelLimit(event: SetChannelLimitEvent[PircBotX]) {
			printAsyncMessage(event.getClass().getName());
		}

		@throws(classOf[Exception])
		override def onSetInviteOnly(event: SetInviteOnlyEvent[PircBotX]) {
			printAsyncMessage(event.getClass().getName());
		}

		@throws(classOf[Exception])
		override def onSetModerated(event: SetModeratedEvent[PircBotX]) {
			printAsyncMessage(event.getClass().getName());
		}

		@throws(classOf[Exception])
		override def onSetNoExternalMessages(
			event: SetNoExternalMessagesEvent[PircBotX]) {
			printAsyncMessage(event.getClass().getName());
		}

		@throws(classOf[Exception])
		override def onSetPrivate(event: SetPrivateEvent[PircBotX]) {
			printAsyncMessage(event.getClass().getName());
		}

		@throws(classOf[Exception])
		override def onSetSecret(event: SetSecretEvent[PircBotX]) {
			printAsyncMessage(event.getClass().getName());
		}

		@throws(classOf[Exception])
		override def onSetTopicProtection(event: SetTopicProtectionEvent[PircBotX]) {
			printAsyncMessage(event.getClass().getName());
		}

		@throws(classOf[Exception])
		override def onSuperOp(event: SuperOpEvent[PircBotX]) {
			printAsyncMessage(event.getClass().getName());
		}

		@throws(classOf[Exception])
		override def onTime(event: TimeEvent[PircBotX]) {
			printAsyncMessage(event.getClass().getName());
		}

		@throws(classOf[Exception])
		override def onUnknown(event: UnknownEvent[PircBotX]) {
			printAsyncMessage(event.getClass().getName());
		}

		@throws(classOf[Exception])
		override def onUserMode(event: UserModeEvent[PircBotX]) {
			printAsyncMessage(event.getClass().getName());
		}

		@throws(classOf[Exception])
		override def onVersion(event: VersionEvent[PircBotX]) {
			printAsyncMessage(event.getClass().getName());
		}

		@throws(classOf[Exception])
		override def onVoice(event: VoiceEvent[PircBotX]) {
			printAsyncMessage(event.getClass().getName());
		}

		@throws(classOf[Exception])
		override def onGenericCTCPCommand(event: GenericCTCPCommand[PircBotX]) {
			printAsyncMessage(event.getClass().getName());
		}

		@throws(classOf[Exception])
		override def onGenericUserMode(event: GenericUserModeEvent[PircBotX]) {
			printAsyncMessage(event.getClass().getName());
		}

		@throws(classOf[Exception])
		override def onGenericChannelMode(event: GenericChannelModeEvent[PircBotX]) {
			printAsyncMessage(event.getClass().getName());
		}

		@throws(classOf[Exception])
		override def onGenericDCC(event: GenericDCCEvent[PircBotX]) {
			printAsyncMessage(event.getClass().getName());
		}

		@throws(classOf[Exception])
		override def onGenericMessage(event: GenericMessageEvent[PircBotX]) {
			printAsyncMessage(event.getMessage());
		}

		@throws(classOf[Exception])
		override def onQuit(event: QuitEvent[PircBotX]) {
			Display.getDefault().asyncExec(new Runnable() {
				def run = {
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
