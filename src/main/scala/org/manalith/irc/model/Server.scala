package org.manalith.irc.model;

case class Server(
	hostname: String, port: Int, encoding: String, verbose: Boolean, password: String, defaultChannels: java.util.List[String], nickname: String) {

}
