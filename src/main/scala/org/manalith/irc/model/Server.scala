package org.manalith.irc.model;

case class Server(
	hostname: String,
	port: Int,
	encoding: String,
	verbose: Boolean,
	password: String,
	defaultChannels: Iterable[Channel],
	nickname: String,
	login: String) {

	/**
	 * Hostname과 Port를 비교하여 동일하면 true를 반환한다.
	 */
	override def equals(obj: Any): Boolean = {
		if (obj.isInstanceOf[Server]) {
			val server = obj.asInstanceOf[Server];

			return server.hostname == hostname && server.port == port;
		}

		return false;
	}
}
