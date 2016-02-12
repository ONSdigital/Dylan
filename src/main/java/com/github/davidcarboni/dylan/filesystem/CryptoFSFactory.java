package com.github.davidcarboni.dylan.filesystem;

import com.sun.jndi.toolkit.url.Uri;
import org.apache.sshd.common.file.FileSystemFactory;
import org.apache.sshd.common.session.Session;

import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;

/**
 * Created by david on 12/02/16.
 */
public class CryptoFSFactory implements FileSystemFactory {
    @Override
    public FileSystem createFileSystem(Session session) throws IOException {
        CryptoFSProvider cryptoFSProvider = CryptoFSProvider.getInstance();
        URI uri = URI.create(cryptoFSProvider.getScheme()+"///");
        return cryptoFSProvider.getFileSystem(uri);
    }
}
