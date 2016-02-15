package com.github.davidcarboni.dylan.filesystem;

import org.apache.commons.lang3.StringUtils;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.net.URI;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.FileAttributeView;
import java.nio.file.spi.FileSystemProvider;
import java.util.Map;
import java.util.Set;

/**
 * TODO: implement.
 */
public class CryptoFSProvider extends FileSystemProvider {

    public static String SCHEME = "crypto";
    static Path root = Paths.get("/");
    static FileSystem fileSystem = CryptoFSProvider.root.getFileSystem();
    static FileSystemProvider fileSystemProvider = fileSystem.provider();
    static CryptoFSProvider instance;

    public CryptoFSProvider() {
        if (instance != null)
            System.out.println("Warning, more than one instatiation detected for " + this.getClass().getSimpleName());
        instance = this;
        System.out.println("Initialised " + this.getClass().getSimpleName());
    }

    @Override
    public String getScheme() {
        return SCHEME;
    }

    /**
     * Calls the underlying {@link FileSystemProvider} and passes the result to {@link CryptoFS#wrap(FileSystem)}
     *
     * @param uri Passed through.
     * @param env Passed through.
     * @return {@link CryptoFS#wrap(FileSystem)}
     * @throws IOException Passed through.
     */
    @Override
    public FileSystem newFileSystem(URI uri, Map<String, ?> env) throws IOException {
        return CryptoFS.wrap(fileSystemProvider.newFileSystem(uri, env));
    }

    /**
     * @param uri Passed through.
     * @return {@link CryptoFS#wrap(FileSystem)}.
     */
    @Override
    public FileSystem getFileSystem(URI uri) {
        return CryptoFS.wrap(fileSystemProvider.getFileSystem(URI.create("file:///")));
    }

    /**
     * TODO
     *
     * @param uri Passed through.
     * @return Passed through.
     */
    @Override
    public Path getPath(URI uri) {
        return CryptoPath.wrap(fileSystemProvider.getPath(uri),CryptoFS.wrap(fileSystem));
    }

    /**
     * TODO
     *
     * @param path    Passed through.
     * @param options Passed through.
     * @param attrs   Passed through.
     * @return Passed through.
     * @throws IOException Passed through.
     */
    @Override
    public SeekableByteChannel newByteChannel(Path path, Set<? extends OpenOption> options, FileAttribute<?>... attrs) throws IOException {
        SecretKey key = CryptoPath.getKey(path);
        SeekableByteChannel channel = fileSystemProvider.newByteChannel(CryptoPath.unwrap(path),options,attrs);
        return CipherChannel.wrap(channel, key);
    }

    /**
     * This is currently a simple pass-through to the underlying {@link FileSystemProvider}.
     *
     * @param dir    Passed through.
     * @param filter Passed through.
     * @return Passed through.
     * @throws IOException Passed through.
     */
    @Override
    public DirectoryStream<Path> newDirectoryStream(Path dir, DirectoryStream.Filter<? super Path> filter) throws IOException {
        return fileSystemProvider.newDirectoryStream(dir, filter);
    }

    /**
     * This is currently a simple pass-through to the underlying {@link FileSystemProvider}.
     *
     * @param dir   Passed through.
     * @param attrs Passed through.
     * @throws IOException Passed through.
     */
    @Override
    public void createDirectory(Path dir, FileAttribute<?>... attrs) throws IOException {
        fileSystemProvider.createDirectory(dir, attrs);
    }

    /**
     * This is currently a simple pass-through to the underlying {@link FileSystemProvider}.
     *
     * @param path Passed through.
     * @throws IOException Passed through.
     */
    @Override
    public void delete(Path path) throws IOException {
        fileSystemProvider.delete(path);
    }

    /**
     * This is currently a simple pass-through to the underlying {@link FileSystemProvider}.
     *
     * @param source  Passed through.
     * @param target  Passed through.
     * @param options Passed through.
     * @throws IOException Passed through.
     */
    @Override
    public void copy(Path source, Path target, CopyOption... options) throws IOException {
        fileSystemProvider.copy(source, target, options);
    }

    /**
     * This is currently a simple pass-through to the underlying {@link FileSystemProvider}.
     *
     * @param source  Passed through.
     * @param target  Passed through.
     * @param options Passed through.
     * @throws IOException Passed through.
     */
    @Override
    public void move(Path source, Path target, CopyOption... options) throws IOException {
        fileSystemProvider.move(source, target, options);
    }

    /**
     * This is currently a simple pass-through to the underlying {@link FileSystemProvider}.
     *
     * @param path  Passed through.
     * @param path2 Passed through.
     * @return Passed through.
     * @throws IOException
     */
    @Override
    public boolean isSameFile(Path path, Path path2) throws IOException {
        return fileSystemProvider.isSameFile(path, path2);
    }

    /**
     * This is currently a simple pass-through to the underlying {@link FileSystemProvider}.
     *
     * @param path Passed through.
     * @return Passed through.
     * @throws IOException Passed through.
     */
    @Override
    public boolean isHidden(Path path) throws IOException {
        return fileSystemProvider.isHidden(path);
    }

    /**
     * This is currently a simple pass-through to the underlying {@link FileSystemProvider}.
     *
     * @param path Passed through.
     * @return Passed through.
     * @throws IOException Passed through.
     */
    @Override
    public FileStore getFileStore(Path path) throws IOException {
        return fileSystemProvider.getFileStore(path);
    }

    /**
     * This is currently a simple pass-through to the underlying {@link FileSystemProvider}.
     *
     * @param path  Passed through.
     * @param modes Passed through.
     * @throws IOException Passed through.
     */
    @Override
    public void checkAccess(Path path, AccessMode... modes) throws IOException {
        fileSystemProvider.checkAccess(path, modes);
    }

    /**
     * This is currently a simple pass-through to the underlying {@link FileSystemProvider}.
     *
     * @param path    Passed through.
     * @param type    Passed through.
     * @param options Passed through.
     * @param <V>     Passed through.
     * @return Passed through.
     */
    @Override
    public <V extends FileAttributeView> V getFileAttributeView(Path path, Class<V> type, LinkOption... options) {
        return fileSystemProvider.getFileAttributeView(path, type, options);
    }

    /**
     * This is currently a simple pass-through to the underlying {@link FileSystemProvider}.
     *
     * @param path    Passed through.
     * @param type    Passed through.
     * @param options Passed through.
     * @param <A>     Passed through.
     * @return Passed through.
     * @throws IOException Passed through.
     */
    @Override
    public <A extends BasicFileAttributes> A readAttributes(Path path, Class<A> type, LinkOption... options) throws IOException {
        return fileSystemProvider.readAttributes(path, type, options);
    }

    /**
     * This is currently a simple pass-through to the underlying {@link FileSystemProvider}.
     *
     * @param path       Passed through.
     * @param attributes Passed through.
     * @param options    Passed through.
     * @return Passed through.
     * @throws IOException Passed through.
     */
    @Override
    public Map<String, Object> readAttributes(Path path, String attributes, LinkOption... options) throws IOException {
        return fileSystemProvider.readAttributes(path, attributes, options);
    }

    /**
     * This is currently a simple pass-through to the underlying {@link FileSystemProvider}.
     *
     * @param path      Passed through.
     * @param attribute Passed through.
     * @param value     Passed through.
     * @param options   Passed through.
     * @throws IOException Passed through.
     */
    @Override
    public void setAttribute(Path path, String attribute, Object value, LinkOption... options) throws IOException {
        fileSystemProvider.setAttribute(path, attribute, value, options);
    }
}
