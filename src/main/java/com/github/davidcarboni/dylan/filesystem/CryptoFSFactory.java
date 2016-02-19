package com.github.davidcarboni.dylan.filesystem;

import org.apache.sshd.common.file.FileSystemFactory;
import org.apache.sshd.common.session.Session;

import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.ProviderNotFoundException;

import static com.github.davidcarboni.dylan.Configuration.CSDB.getCsdbDataDir;

/**
 * {@link FileSystemFactory} implementation.
 */
public class CryptoFSFactory implements FileSystemFactory {
    @Override
    public CryptoFS createFileSystem(Session session) throws IOException {
        URI uri = URI.create(CryptoFSProvider.SCHEME+":///");
        System.out.println("uri = " + uri);
        try {
            return (CryptoFS) FileSystems.getFileSystem(uri);
        } catch (ProviderNotFoundException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public static void main(String[] args) throws IOException {
        CryptoFSFactory cryptoFSFactory = new CryptoFSFactory();
        CryptoFS fileSystem = cryptoFSFactory.createFileSystem(null);
        //Path path = fileSystem.getPath("/home/david");
        Path path = fileSystem.getPath("/Users/dave/Desktop");
        URI uri = path.toUri();
        System.out.println("uri = " + uri);
       // Path path2 = fileSystem.getPath("/home/david");
        Path path2 = fileSystem.getPath(getCsdbDataDir().toString());
        URI uri2 = path2.toUri();
        System.out.println("uri = " + uri2);
    }
}
