package com.github.davidcarboni.dylan;

import com.github.davidcarboni.cryptolite.KeyExchange;
import com.github.davidcarboni.dylan.api.HttpSupplier;
import com.github.davidcarboni.dylan.filesystem.CryptoFS;
import com.github.davidcarboni.dylan.filesystem.CryptoPath;
import com.github.davidcarboni.dylan.notify.Notifier;
import com.github.davidcarboni.dylan.sshd.SSHServer;
import com.github.davidcarboni.restolino.framework.Startup;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.function.Consumer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static org.slf4j.LoggerFactory.getLogger;

public class CSDBImport implements Startup {

	private static final Logger log = getLogger(CSDBImport.class);

	private FileSystem cryptoFileSystem = FileSystems.getFileSystem(CryptoFS.uri());
	private HttpSupplier httpSupplier = new HttpSupplier();

	private Consumer<Path> setScpFileReceivedHandler = (Path path) -> {
		File csdbFile = new File(Store.files.toString());

		if (!csdbFile.exists() && !csdbFile.mkdirs()) {
			throw new RuntimeException("csdb path not found");
		}

		try {
			processFile(path, csdbFile);
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

	private void processFile(Path path, File csdbFile) throws IOException {
		log.info("Received file: {}", path.toString());
		Path csdbPath = cryptoFileSystem.getPath(csdbFile.getPath());
		if (FilenameUtils.getExtension(path.toString()).equalsIgnoreCase("zip")) {
			log.info("Processing as a zip file: {}", path.toString());
            processZipFile(path, csdbPath);
        } else {
			log.info("Processing as a single file: {}", path.toString());
            processSingleFile(path, csdbPath);
        }
	}

	private void processSingleFile(Path path, Path csdbPath) throws IOException {
		try(InputStream inputStream = Files.newInputStream(path)) {
            String filename = FilenameUtils.getName(path.toString());
			processFile(csdbPath, inputStream, filename);
        }
	}

	private void processZipFile(Path path, Path csdbPath) throws IOException {
		ZipInputStream zis = new ZipInputStream(Files.newInputStream(path));
		ZipEntry entry;

		while ((entry = zis.getNextEntry()) != null) {
            log.info("Processing file from zip: {}", entry.getName());
            if (!entry.isDirectory()) {
				processFile(csdbPath, zis, entry.getName());
            }
        }
	}

	private void processFile(Path csdbPath, InputStream inputStream, String filename) throws IOException {
		Path destinationPath = resolveFile(csdbPath, filename);
		Files.copy(inputStream, destinationPath, StandardCopyOption.REPLACE_EXISTING);
		String encryptedKey = new KeyExchange().encryptKey(CryptoPath.getKey(destinationPath), Store.getRecipientKey().get());
		Store.saveKey(filename, encryptedKey);
		Notifier.notify(CryptoPath.unwrap(destinationPath), httpSupplier);
	}

	private Path resolveFile(Path path, String name) {
		return path.resolve(name);
	}

	private String keyName(Path p) {
		return String.format("%s.%s", FilenameUtils.getBaseName(CryptoPath.unwrap(p).getFileName().toString()), "key");
	}

	@Override
	public void init() {
		try {
			log.info("CSDB Import init. Starting SSH server...");
			new SSHServer(setScpFileReceivedHandler).start();
		} catch (IOException e) {
			log.error("Failed to start SSH server", e);
		}
	}
}
