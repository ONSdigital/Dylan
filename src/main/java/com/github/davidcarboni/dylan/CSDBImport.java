package com.github.davidcarboni.dylan;

import com.github.davidcarboni.cryptolite.KeyExchange;
import com.github.davidcarboni.dylan.filesystem.CryptoFS;
import com.github.davidcarboni.dylan.filesystem.CryptoPath;
import com.github.davidcarboni.dylan.sshd.SSHServer;
import com.github.davidcarboni.restolino.framework.Startup;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Consumer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class CSDBImport implements Startup {

	private FileSystem cryptoFileSystem = FileSystems.getFileSystem(CryptoFS.uri());
	private FileSystem defaultFileSystem = FileSystems.getDefault();

	// Encrypt example.
	//new Crypto().decrypt(Files.newInputStream(path), CryptoPath.getKey(path)


	private Consumer<Path> setScpFileReceivedHandler = (Path path) -> {
		System.out.println("Received zip file: " + path.toString());
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
					Path dest = csdbPath.resolve(entry.getName());
					Files.copy(zis, dest);
					System.out.println("CSDB file encrypted written to: " + dest.toString());

					String encryptedKey = new KeyExchange().encryptKey(CryptoPath.getKey(dest), Store.getRecipientKey());
					Store.saveKey(entry.getName(), encryptedKey);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	};

	@Override
	public void init() {
		try {
			new SSHServer(setScpFileReceivedHandler).start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
