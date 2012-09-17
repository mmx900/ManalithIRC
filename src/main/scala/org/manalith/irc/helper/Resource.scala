package org.manalith.irc.helper

import java.io.BufferedInputStream
import java.io.IOException
import java.io.InputStream
import java.util.Collections
import java.util.Locale
import java.util.MissingResourceException
import java.util.Properties
import java.util.ResourceBundle

object Resource {
	var messages: ResourceBundle = null;
	var application: ResourceBundle = null;

	try {
		messages = ResourceBundle.getBundle("Messages", new XMLResourceBundleControl());
		application = ResourceBundle.getBundle("Application", new XMLResourceBundleControl());
	} catch {
		case ex: MissingResourceException => ()
	}

	private class XMLResourceBundleControl extends ResourceBundle.Control {
		private val XML = "xml";

		override def getFormats(baseName: String): java.util.List[String] = {
			return Collections.singletonList(XML);
		}

		@throws(classOf[IOException])
		@throws(classOf[IllegalAccessException])
		@throws(classOf[InstantiationException])
		override def newBundle(baseName: String, locale: Locale, format: String,
			loader: ClassLoader, reload: Boolean): ResourceBundle = {

			if ((baseName == null) || (locale == null) || (format == null) || (loader == null)) {
				throw new NullPointerException();
			}
			var bundle: ResourceBundle = null;
			if (!format.equals(XML)) {
				return null;
			}

			var bundleName = toBundleName(baseName, locale);
			var resourceName = toResourceName(bundleName, format);
			var url = loader.getResource(resourceName);
			if (url == null) {
				return null;
			}
			var connection = url.openConnection();
			if (connection == null) {
				return null;
			}
			if (reload) {
				connection.setUseCaches(false);
			}
			var stream = connection.getInputStream();
			if (stream == null) {
				return null;
			}
			var bis = new BufferedInputStream(stream);
			bundle = new XMLResourceBundle(bis);
			bis.close();

			return bundle;
		}
	}

	@throws(classOf[IOException])
	private class XMLResourceBundle(stream: InputStream) extends ResourceBundle {
		val props = new Properties();
		props.loadFromXML(stream);

		protected def handleGetObject(key: String): Object = {
			return props.getProperty(key);
		}

		def getKeys(): java.util.Enumeration[String] = {
			var handleKeys = props.stringPropertyNames();
			return Collections.enumeration(handleKeys);
		}
	}

}