package com.github.davidcarboni.dylan.filesystem;

import com.github.davidcarboni.cryptolite.Keys;

import javax.crypto.SecretKey;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * TODO: implement.
 */
public class CryptoPath implements Path {

    private static Map<Path, SecretKey> keys = new ConcurrentHashMap<>();

    private Path path;
    private CryptoFS filesystem;

    static Path wrap(Path path, CryptoFS filesystem) {
        // No point double-wrapping:
        if (CryptoPath.class.isAssignableFrom(path.getClass())) {
            System.out.println("Not double-wrapping " + path);
            return path;
        }
        return new CryptoPath(path, filesystem);
    }

    static Path unwrap(Path path) {
        String ssp = path.toUri().getSchemeSpecificPart();
        return Paths.get(ssp);
    }

    private CryptoPath(Path path, CryptoFS filesystem) {
        this.filesystem = filesystem;
        this.path = unwrap(path);
        // Attempt to ensure we have a canonical path:
        SecretKey current = keys.putIfAbsent(this, Keys.newSecretKey());
        if (current == null)
            System.out.println("Generated a key for " + this.toUri());
        else
            System.out.println("Using existing key for " + this.toUri());
    }

    public static SecretKey getKey(Path path) {
        return keys.get(path);
    }

    @Override
    public FileSystem getFileSystem() {
        return filesystem;
    }

    @Override
    public boolean isAbsolute() {
        return path.isAbsolute();
    }

    @Override
    public Path getRoot() {
        return CryptoPath.wrap(path.getRoot(), filesystem);
    }

    @Override
    public Path getFileName() {
        return CryptoPath.wrap(path.getFileName(), filesystem);
    }

    @Override
    public Path getParent() {
        return CryptoPath.wrap(path.getParent(), filesystem);
    }

    @Override
    public int getNameCount() {
        return path.getNameCount();
    }

    @Override
    public Path getName(int index) {
        return CryptoPath.wrap(path.getName(index), filesystem);
    }

    @Override
    public Path subpath(int beginIndex, int endIndex) {
        return CryptoPath.wrap(path.subpath(beginIndex, endIndex), filesystem);
    }

    @Override
    public boolean startsWith(Path other) {
        return path.startsWith(unwrap(other));
    }

    @Override
    public boolean startsWith(String other) {
        return path.startsWith(other);
    }

    @Override
    public boolean endsWith(Path other) {
        return path.endsWith(unwrap(other));
    }

    @Override
    public boolean endsWith(String other) {
        return path.endsWith(other);
    }

    @Override
    public Path normalize() {
        return wrap(path.normalize(), filesystem);
    }

    @Override
    public Path resolve(Path other) {
        return wrap(path.resolve(unwrap(other)), filesystem);
    }

    @Override
    public Path resolve(String other) {
        return wrap(path.resolve(other), filesystem);
    }

    @Override
    public Path resolveSibling(Path other) {
        return wrap(path.resolveSibling(unwrap(other)), filesystem);
    }

    @Override
    public Path resolveSibling(String other) {
        return wrap(path.resolveSibling(other), filesystem);
    }

    @Override
    public Path relativize(Path other) {
        return wrap(path.relativize(unwrap(other)), filesystem);
    }

    @Override
    public URI toUri() {
        try {
            return new URI(CryptoFSProvider.SCHEME, path.toUri().getSchemeSpecificPart(), null);
        } catch (URISyntaxException e) {
            throw new RuntimeException("Unexpected error constructing URI for " + path.toUri(), e);
        }
    }

    @Override
    public Path toAbsolutePath() {
        return wrap(path.toAbsolutePath(), filesystem);
    }

    @Override
    public Path toRealPath(LinkOption... options) throws IOException {
        return wrap(path.toRealPath(options), filesystem);
    }

    @Override
    public File toFile() {
        return path.toFile();
    }

    @Override
    public WatchKey register(WatchService watcher, WatchEvent.Kind<?>[] events, WatchEvent.Modifier... modifiers) throws IOException {
        // This may need further attention to work correctly:
        return path.register(watcher, events, modifiers);
    }

    @Override
    public WatchKey register(WatchService watcher, WatchEvent.Kind<?>... events) throws IOException {
        // This may need further attention to work correctly:
        return path.register(watcher, events);
    }

    @Override
    public Iterator<Path> iterator() {
        return new Iterator<Path>() {
            final Iterator<Path> iterator = path.iterator();

            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public Path next() {
                return wrap(iterator.next(), filesystem);
            }
        };
    }

    @Override
    public int compareTo(Path other) {
        return path.compareTo(unwrap(other));
    }

    @Override
    public int hashCode() {
        return path.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        try {
            return o!=null &&
                    CryptoPath.class.isAssignableFrom(o.getClass()) &&
                    Files.isSameFile(path, ((CryptoPath)o).path);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public String toString() {
        return path.toString();
    }
}
