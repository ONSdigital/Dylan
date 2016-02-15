package com.github.davidcarboni.dylan;

import com.github.davidcarboni.cryptolite.KeyWrapper;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Manages the Dylan file store.
 */
public class Store {

	static final Path store = Paths.get(StringUtils.defaultIfBlank(System.getenv("dylan.storage"), "./target/dylan"));
	static final Path keys = store.resolve("keys");
	public static final Path files = store.resolve("files");
	static final Path senderKey = store.resolve("sender.pub");
	static final Path recipientKey = store.resolve("recipient.pub");

	static boolean initialised;

	// TODO do this on create.
	static void initialise() throws IOException {
		if (!initialised) {
			Files.createDirectories(keys);
			Files.createDirectories(files);
			initialised = true;
		}
	}

	public static String getKey(String name) throws IOException {
		initialise();
		String result = null;
		Path path = keys.resolve(name);
		if (Files.isRegularFile(path)) {
			result = FileUtils.readFileToString(path.toFile(), UTF_8);
		}
		return result;
	}

	public static void saveKey(String name, String encryptedKey) throws IOException {
		initialise();
		Path target = keys.resolve(name);
		Path keyFile = Files.createTempFile(name, ".key");
		FileUtils.writeStringToFile(keyFile.toFile(), encryptedKey, StandardCharsets.UTF_8);
		Files.move(keyFile, target, StandardCopyOption.REPLACE_EXISTING);
	}

	public static InputStream getFile(String name) throws IOException {
		initialise();
		InputStream result = null;
		Path path = files.resolve(name);
		if (Files.isRegularFile(path)) {
			result = new BufferedInputStream(Files.newInputStream(path));
		}
		return result;
	}

	public static PublicKey getSenderKey() throws IOException {
		initialise();
		PublicKey result = null;
		if (Files.isRegularFile(senderKey)) {
			String encodedKey = FileUtils.readFileToString(senderKey.toFile(), UTF_8);
			result = KeyWrapper.decodePublicKey(encodedKey);
		}
		return result;
	}

	public static PublicKey getRecipientKey() throws IOException {
		initialise();
		PublicKey result = null;
		if (Files.isRegularFile(recipientKey)) {
			String encodedKey = FileUtils.readFileToString(recipientKey.toFile(), UTF_8);
			result = KeyWrapper.decodePublicKey(encodedKey);
		}
		return result;
	}

	public static void saveRecipientKey(PublicKey publicKey) throws IOException {
		initialise();
		Path keyFile = Files.createTempFile(recipientKey.getFileName().toString(), ".key");
		String encodedKey = KeyWrapper.encodePublicKey(publicKey);
		FileUtils.writeStringToFile(keyFile.toFile(), encodedKey, StandardCharsets.UTF_8);
		Files.move(keyFile, recipientKey, StandardCopyOption.REPLACE_EXISTING);
	}

	public static List<Path> list() throws IOException {
		initialise();
		List<Path> result = new ArrayList<>();
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(files)) {
			for (Path entry : stream) {
				if (Files.isRegularFile(entry)) {
					result.add(entry);
				}
			}
		}
		return result;
	}
}
