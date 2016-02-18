package com.github.davidcarboni.dylan.sshd;

import com.github.davidcarboni.dylan.Configuration;
import com.github.davidcarboni.dylan.filesystem.CryptoFSFactory;
import org.apache.sshd.common.scp.ScpTransferEventListener;
import org.apache.sshd.common.session.Session;
import org.apache.sshd.server.SshServer;
import org.apache.sshd.server.command.ScpCommandFactory;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.apache.sshd.server.session.ServerSession;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermission;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Set;
import java.util.function.Consumer;

import static java.util.Objects.requireNonNull;

public class SSHServer {

	protected PublicKeyAuthenticator publicKeyAuthenticator = null;
	protected String scpRootDir;
	protected Consumer<Path> scpFileReceivedHandler = null;
	protected SshServer sshd;
	protected FileSystem vfs;

	/**
	 * @param scpFileReceivedHandler implementation logic specifying what to do when a file is received.
	 */
	public SSHServer(Consumer<Path> scpFileReceivedHandler) {
		requireNonNull(scpFileReceivedHandler, "scpFileReceivedHandler is required and cannot be null.");

		this.scpFileReceivedHandler = scpFileReceivedHandler;
		this.scpRootDir = Configuration.SCP.getRootDir();
		this.publicKeyAuthenticator = new AuthorizedKeysDecoder(getClass().getResourceAsStream("/authorized_keys"));
	}

	public void start() throws IOException {
		final CryptoFSFactory cryptoFSFactory = new CryptoFSFactory();

		sshd = SshServer.setUpDefaultServer();
		sshd.setPort(Configuration.SSH.getSSHPort());
		sshd.setKeyPairProvider(new SimpleGeneratorHostKeyProvider());

		sshd.setFileSystemFactory((Session session) -> cryptoFSFactory.createFileSystem(session));

		sshd.setPublickeyAuthenticator((String s, PublicKey publicKey, ServerSession serverSession) ->
				publicKeyAuthenticator != null && publicKey instanceof RSAPublicKey && publicKeyAuthenticator.isValid(s, publicKey)
		);

		ScpCommandFactory scp = new ScpCommandFactory();
		scp.addEventListener(new ScpTransferEventListener() {

			@Override
			public void startFileEvent(FileOperation fileOperation, Path path, long l, Set<PosixFilePermission> set) {
				eventMsg("startFileEvent", fileOperation, path);
			}

			@Override
			public void endFileEvent(FileOperation fileOperation, Path path, long l, Set<PosixFilePermission> set, Throwable throwable) {
				eventMsg("endFileEvent", fileOperation, path);

				if (throwable != null) {
					throwable.printStackTrace();
					return;
				}
				scpFileReceivedHandler.accept(path);
			}

			@Override
			public void startFolderEvent(FileOperation fileOperation, Path path, Set<PosixFilePermission> set) {
				eventMsg("startFolderEvent", fileOperation, path);
			}

			@Override
			public void endFolderEvent(FileOperation fileOperation, Path path, Set<PosixFilePermission> set, Throwable throwable) {
				eventMsg("endFolderEvent", fileOperation, path);
				if (throwable != null) {
					throwable.printStackTrace();
				}
			}
		});

		sshd.setCommandFactory(scp);
		sshd.start();
	}

	private void eventMsg(String eventName, ScpTransferEventListener.FileOperation fileOperation, Path path) {
		System.out.println(String.format("%s (%s) %s", eventName, fileOpStr(fileOperation), path));
	}

	private String fileOpStr(ScpTransferEventListener.FileOperation fileOperation) {
		return ScpTransferEventListener.FileOperation.SEND.equals(fileOperation) ? "SEND" : "RECEIVE";
	}
}
