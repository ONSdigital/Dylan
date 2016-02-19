package com.github.davidcarboni.dylan;

import com.github.davidcarboni.httpino.Endpoint;
import com.github.davidcarboni.httpino.Host;
import org.apache.commons.lang3.StringUtils;

import java.nio.file.Paths;


public class Configuration {

	public static final String ZEBEDEE_HOST_STR = StringUtils.defaultIfBlank(System.getenv("recipient.url"), "http://localhost:8082");

	public static final Host RECIPIENT_HOST = new Host(ZEBEDEE_HOST_STR);

	public static final String RECIPIENT_NOTIFY_PATH = "csdbnotify";

	public static Endpoint getEndpoint(Host host, String path) {
		return new Endpoint(host, path);
	}

	public static class SCP {

		private static final String ROOT_DIR_ENV = "SCP_ROOT_DIR";
		private static final String AUTHORIZED_KEYS_ENV = "SCP_AUTHORIZED_KEYS";

		public static String getRootDir() {
			return get(ROOT_DIR_ENV, System.getProperty("user.dir"));
		}

		public static String getAuthorizedKeys() {
			return get(AUTHORIZED_KEYS_ENV, System.getProperty("user.home") + "/.ssh/authorized_keys");
		}
	}

	/**
	 * SSH server configuration.
	 */
	public static class SSH {

		private static final String SSH_PORT_ENV = "SSH_PORT";

		public static int getSSHPort() {
			return Integer.valueOf(get(SSH_PORT_ENV, "2323"));
		}

	}

	public static class CSDB {
		private static final String CSDB_DATA_DIR_ENV = "CSDB_DATA_DIR";

		public static String getCsdbDataDir() {
			return Paths.get(getBaseDir(), "files").toString();
		}

		public static String getCsdbKeyDir() {
			return Paths.get(getBaseDir(), "keys").toString();
		}

		private static String getBaseDir() {
			return get(CSDB_DATA_DIR_ENV, System.getProperty("user.dir") + "/target");
		}
	}

	/**
	 * Gets a configuration value from {@link System#getProperty(String)}, falling back to {@link System#getenv()}
	 * if the property comes back blank.
	 *
	 * @param key The configuration value key.
	 * @return A system property or, if that comes back blank, an environment value.
	 */
	public static String get(String key) {
		return StringUtils.defaultIfBlank(System.getProperty(key), System.getenv(key));
	}

	/**
	 * Gets a configuration value from {@link System#getProperty(String)}, falling back to {@link System#getenv()}
	 * if the property comes back blank, then falling back to the default value.
	 *
	 * @param key          The configuration value key.
	 * @param defaultValue The default to use if neither a property nor an environment value are present.
	 * @return The result of {@link #get(String)}, or <code>defaultValue</code> if that result is blank.
	 */
	public static String get(String key, String defaultValue) {
		return StringUtils.defaultIfBlank(get(key), defaultValue);
	}

}
