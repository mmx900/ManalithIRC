package org.manalith.irc;

import java.io.IOException;

import org.manalith.irc.model.Server;
import org.pircbotx.PircBotX;
import org.pircbotx.exception.IrcException;
import org.pircbotx.exception.NickAlreadyInUseException;

public class Application {
	public Application() {

	}

	public void connect(Server server) {
		PircBotX bot = new PircBotX();
		try {
			bot.connect(server.getHostname(), server.getPort(),
					server.getPassword());
		} catch (NickAlreadyInUseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IrcException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
