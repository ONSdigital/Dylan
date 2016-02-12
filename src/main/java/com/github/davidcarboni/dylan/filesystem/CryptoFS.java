package com.github.davidcarboni.dylan.filesystem;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.UserPrincipalLookupService;
import java.nio.file.spi.FileSystemProvider;
import java.util.Set;

/**
 * TODO: implement.
 */
public class CryptoFS extends FileSystem {

    static CryptoFS instance = new CryptoFS();

    public static CryptoFS getInstance() {
        return instance;
    }

    @Override
    public FileSystemProvider provider() {
        return CryptoFSProvider.getInstance();
    }

    /**
     * Closes the underlying {@link FileSystem}.
     */
    @Override
    public void close() throws IOException {
        CryptoFSProvider.fileSystem.close();
    }

    /**
     * @return Whether the underlying {@link FileSystem} is open.
     */
    @Override
    public boolean isOpen() {
        return CryptoFSProvider.fileSystem.isOpen();
    }

    /**
     * @return Whether the underlying {@link FileSystem} is read-only.
     */
    @Override
    public boolean isReadOnly() {
        return CryptoFSProvider.fileSystem.isReadOnly();
    }

    /**
     * @return The separator of the underlying {@link FileSystem}.
     */
    @Override
    public String getSeparator() {
        return CryptoFSProvider.fileSystem.getSeparator();
    }

    /**
     * This is currently a simple pass-through to the underlying filesystem.
     *
     * @return The result of calling this method on {@link CryptoFSProvider#fileSystem}.
     */
    @Override
    public Iterable<Path> getRootDirectories() {
        return CryptoFSProvider.fileSystem.getRootDirectories();
    }

    /**
     * This is currently a simple pass-through to the underlying filesystem.
     *
     * @return The result of calling this method on {@link CryptoFSProvider#fileSystem}.
     */
    @Override
    public Iterable<FileStore> getFileStores() {
        return CryptoFSProvider.fileSystem.getFileStores();
    }

    /**
     * This is currently a simple pass-through to the underlying filesystem.
     *
     * @return The result of calling this method on {@link CryptoFSProvider#fileSystem}.
     */
    @Override
    public Set<String> supportedFileAttributeViews() {
        return CryptoFSProvider.fileSystem.supportedFileAttributeViews();
    }

    /**
     * TODO
     *
     * @param first
     * @param more
     * @return
     */
    @Override
    public Path getPath(String first, String... more) {
        return null;
    }

    /**
     * This is currently a simple pass-through to the underlying filesystem.
     *
     * @param syntaxAndPattern Passed to the underlying {@link FileSystem}.
     * @return The result of calling this method on {@link CryptoFSProvider#fileSystem}.
     */
    @Override
    public PathMatcher getPathMatcher(String syntaxAndPattern) {
        return CryptoFSProvider.fileSystem.getPathMatcher(syntaxAndPattern);
    }

    /**
     * This is currently a simple pass-through to the underlying filesystem.
     *
     * @return The result of calling this method on {@link CryptoFSProvider#fileSystem}.
     */
    @Override
    public UserPrincipalLookupService getUserPrincipalLookupService() {
        return CryptoFSProvider.fileSystem.getUserPrincipalLookupService();
    }

    /**
     * This is currently a simple pass-through to the underlying filesystem.
     *
     * @return The result of calling this method on {@link CryptoFSProvider#fileSystem}.
     * @throws IOException If the underlying {@link FileSystem} throws an exception.
     */
    @Override
    public WatchService newWatchService() throws IOException {
        return CryptoFSProvider.fileSystem.newWatchService();
    }
}
