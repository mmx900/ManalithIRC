package org.manalith.irc.model;

import java.util.List;

public class Server {
	private String hostname;

	private int port;

	private String encoding;

	private boolean verbose;

	private String password;

	private List<String> defaultChannels;

	private String nickname;
	public List<String> getDefaultChannels() {
		return defaultChannels;
	}
	public String getEncoding() {
		return encoding;
	}

	public String getHostname() {
		return hostname;
	}

	public String getNickname() {
		return nickname;
	}

	public String getPassword() {
		return password;
	}

	public int getPort() {
		return port;
	}

	public boolean isVerbose() {
		return verbose;
	}

	public void setDefaultChannels(List<String> defaultChannels) {
		this.defaultChannels = defaultChannels;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}
}
