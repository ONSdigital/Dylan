package com.github.davidcarboni.dylan.filesystem;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.UserPrincipalLookupService;
import java.nio.file.spi.FileSystemProvider;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * TODO: implement.
 */
public class CryptoFS extends FileSystem {

    private static Map<FileSystem, CryptoFS> instances = new ConcurrentHashMap<>();
    private FileSystem fileSystem;

    public CryptoFS(FileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }


    public static CryptoFS wrap(FileSystem fileSystem) {
        if (!instances.containsKey(fileSystem))
            instances.putIfAbsent(fileSystem, new CryptoFS(fileSystem));
        return instances.get(fileSystem);
    }

    @Override
    public FileSystemProvider provider() {
        return CryptoFSProvider.instance;
    }

    /**
     * Closes the underlying {@link FileSystem}.
     */
    @Override
    public void close() throws IOException {
        fileSystem.close();
    }

    /**
     * @return Whether the underlying {@link FileSystem} is open.
     */
    @Override
    public boolean isOpen() {
        return fileSystem.isOpen();
    }

    /**
     * @return Whether the underlying {@link FileSystem} is read-only.
     */
    @Override
    public boolean isReadOnly() {
        return fileSystem.isReadOnly();
    }

    /**
     * @return The separator of the underlying {@link FileSystem}.
     */
    @Override
    public String getSeparator() {
        return fileSystem.getSeparator();
    }

    /**
     * This is currently a simple pass-through to the underlying {@link FileSystem}.
     *
     * @return The result of calling this method on {@link CryptoFSProvider#fileSystem}.
     */
    @Override
    public Iterable<Path> getRootDirectories() {
        return fileSystem.getRootDirectories();
    }

    /**
     * This is currently a simple pass-through to the underlying {@link FileSystem}.
     *
     * @return The result of calling this method on {@link CryptoFSProvider#fileSystem}.
     */
    @Override
    public Iterable<FileStore> getFileStores() {
        return fileSystem.getFileStores();
    }

    /**
     * This is currently a simple pass-through to the underlying {@link FileSystem}.
     *
     * @return The result of calling this method on {@link CryptoFSProvider#fileSystem}.
     */
    @Override
    public Set<String> supportedFileAttributeViews() {
        return fileSystem.supportedFileAttributeViews();
    }

    /**
     * TODO
     *
     * @param first Coming soon..
     * @param more  Coming soon..
     * @return A {@link CryptoPath}.
     */
    @Override
    public Path getPath(String first, String... more) {
        return CryptoPath.wrap(Paths.get(first, more), this);
    }

    /**
     * This is currently a simple pass-through to the underlying {@link FileSystem}.
     *
     * @param syntaxAndPattern Passed to the underlying {@link FileSystem}.
     * @return The result of calling this method on {@link CryptoFSProvider#fileSystem}.
     */
    @Override
    public PathMatcher getPathMatcher(String syntaxAndPattern) {
        return fileSystem.getPathMatcher(syntaxAndPattern);
    }

    /**
     * This is currently a simple pass-through to the underlying {@link FileSystem}.
     *
     * @return The result of calling this method on {@link CryptoFSProvider#fileSystem}.
     */
    @Override
    public UserPrincipalLookupService getUserPrincipalLookupService() {
        return fileSystem.getUserPrincipalLookupService();
    }

    /**
     * This is currently a simple pass-through to the underlying {@link FileSystem}.
     *
     * @return The result of calling this method on {@link CryptoFSProvider#fileSystem}.
     * @throws IOException If the underlying {@link FileSystem} throws an exception.
     */
    @Override
    public WatchService newWatchService() throws IOException {
        return fileSystem.newWatchService();
    }
}
