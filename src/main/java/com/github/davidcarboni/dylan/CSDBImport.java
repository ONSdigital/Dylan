package com.github.davidcarboni.dylan;

import com.github.davidcarboni.dylan.sshd.SSHServer;
import com.github.davidcarboni.restolino.framework.Startup;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Consumer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class CSDBImport implements Startup {

	private Consumer<Path> setScpFileReceivedHandler = (Path path) -> {
		System.out.println("Received zip file: " + path.toString());
		//CryptoFS cryptoFS =  CryptoFS.getInstance();

		//String p = Configuration.CSDB.getCsdbDataDir();
		File csdbFile = new File(Store.files.toString());

		if (!csdbFile.exists() && !csdbFile.mkdirs()) {
			throw new RuntimeException("csdb path not found");
		}

		//Path csdbPath = FileSystems.getDefault().getPath(csdbFile.getPath());
		Path csdbPath = FileSystems.getDefault().getPath(csdbFile.getPath());

		try {
			byte bytes[] = Files.readAllBytes(FileSystems.getDefault().getPath(Configuration.SCP.getRootDir() + path.toString()));
			ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(bytes));
			ZipEntry entry;

			while ((entry = zis.getNextEntry()) != null) {
				System.out.println("Received CSDB file: " + entry.getName());

				if (!entry.isDirectory()) {
					Path dest = csdbPath.resolve(entry.getName());
					Files.copy(zis, dest);
					System.out.println("CSDB file written to: " + dest.toString());
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
			;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
