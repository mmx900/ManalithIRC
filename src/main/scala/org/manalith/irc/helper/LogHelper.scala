package org.manalith.irc.helper

import org.apache.log4j.Logger

trait LogHelper {
	lazy val logger = Logger.getLogger(this.getClass.getName)
}