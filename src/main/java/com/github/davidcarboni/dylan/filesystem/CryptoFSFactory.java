package com.github.davidcarboni.dylan.filesystem;

import com.sun.jndi.toolkit.url.Uri;
import org.apache.sshd.common.file.FileSystemFactory;
import org.apache.sshd.common.session.Session;

import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;

/**
 * {@link FileSystemFactory} implementation.
 */
public class CryptoFSFactory implements FileSystemFactory {
    @Override
    public CryptoFS createFileSystem(Session session) throws IOException {
        URI uri = URI.create(CryptoFSProvider.SCHEME+":///");
        System.out.println("uri = " + uri);
        return (CryptoFS)FileSystems.getFileSystem(uri);
    }

    public static void main(String[] args) throws IOException {
        CryptoFSFactory cryptoFSFactory = new CryptoFSFactory();
        CryptoFS fileSystem = cryptoFSFactory.createFileSystem(null);
        Path path = fileSystem.getPath("/home/david");
        URI uri = path.toUri();
        System.out.println("uri = " + uri);
        Path path2 = fileSystem.getPath("/home/david");
        URI uri2 = path2.toUri();
        System.out.println("uri = " + uri2);
    }
}
