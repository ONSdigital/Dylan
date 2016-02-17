package com.github.davidcarboni.dylan;

import com.github.davidcarboni.cryptolite.KeyExchange;
import com.github.davidcarboni.dylan.filesystem.CryptoFS;
import com.github.davidcarboni.dylan.filesystem.CryptoPath;
import com.github.davidcarboni.dylan.notify.Notifier;
import com.github.davidcarboni.dylan.sshd.SSHServer;
import com.github.davidcarboni.restolino.framework.Startup;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;


public class CSDBImport implements Startup {

	private FileSystem cryptoFileSystem = FileSystems.getFileSystem(CryptoFS.uri());

	private Consumer<Path> setScpFileReceivedHandler = (Path path) -> {
		File csdbFile = new File(Store.files.toString());

		if (!csdbFile.exists() && !csdbFile.mkdirs()) {
			throw new RuntimeException("csdb path not found");
		}

		try {
			Path csdbPath = cryptoFileSystem.getPath(csdbFile.getPath());

			ZipInputStream zis = new ZipInputStream(Files.newInputStream(path));
			ZipEntry entry;

			while ((entry = zis.getNextEntry()) != null) {
				System.out.println("Received CSDB file: " + entry.getName());

				if (!entry.isDirectory()) {
					Path dest = resolveFile(csdbPath, entry.getName());

					Files.copy(zis, dest);

					String encryptedKey = new KeyExchange().encryptKey(CryptoPath.getKey(dest), Store.getRecipientKey());
					Store.saveKey(keyName(dest), encryptedKey);

					Notifier.notify(dest);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally {
			try {
				// TODO is this necessary? Should this be kept/moved/archived?
				Files.delete(path);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	};

	private Path resolveFile(Path path, String name) {
		// TODO REMOVE the UUID from the name - this is for local dev only. Allows us to upload the same file multiple times.
		return path.resolve(String.format("%s-%s.%s", FilenameUtils.getBaseName(name), UUID.randomUUID(),
				FilenameUtils.getExtension(name)));
	}

	private String keyName(Path p) {
		return String.format("%s.%s", FilenameUtils.getBaseName(CryptoPath.unwrap(p).getFileName().toString()), "key");
	}

	@Override
	public void init() {
		try {
			new SSHServer(setScpFileReceivedHandler).start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
