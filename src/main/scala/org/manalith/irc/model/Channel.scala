package org.manalith.irc.model;

case class Channel(
	name: String,
	password: String) {

	/**
	 * 채널 이름을 비교하여 동일하면 true를 반환한다.
	 */
	override def equals(obj: Any): Boolean = {
		if (obj.isInstanceOf[Server]) {
			val channel = obj.asInstanceOf[Channel];

			return channel.name == name;
		}

		return false;
	}
}
