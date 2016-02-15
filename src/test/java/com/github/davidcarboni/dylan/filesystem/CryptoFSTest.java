package com.github.davidcarboni.dylan.filesystem;

import com.github.davidcarboni.cryptolite.ByteArray;
import com.github.davidcarboni.cryptolite.Keys;
import com.github.davidcarboni.cryptolite.Random;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.crypto.SecretKey;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.channels.Channels;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.*;
import java.util.*;

/**
 * Created by david on 12/02/16.
 */
public class CryptoFSTest {

    @Before
    public  void before() throws IOException {
        Path path = Paths.get("test.enc");
        if (Files.exists(path)) Files.delete(path);
    }

    @After
    public  void after() throws IOException {
        Path path = Paths.get("test.enc");
        if (Files.exists(path)) Files.delete(path);
    }

    @Test
    public void shouldWork() throws IOException {

        // Given
        URI uri = URI.create("crypto://test.enc");
        SecretKey key = Keys.newSecretKey();
        byte[] data = Random.bytes(10);
        CryptoFSFactory cryptoFSFactory = new CryptoFSFactory();
        CryptoFS fileSystem = cryptoFSFactory.createFileSystem(null);
        Set<OpenOption> options = new HashSet<>();
        options.add(StandardOpenOption.READ);
        options.add(StandardOpenOption.WRITE);
        options.add(StandardOpenOption.CREATE_NEW);

        // When
        Path path = fileSystem.getPath("test.enc");
        try (OutputStream outputStream = Channels.newOutputStream(fileSystem.provider().newByteChannel(path, options))) {
            IOUtils.copy(new ByteArrayInputStream(data), outputStream);
        }
        ByteArrayOutputStream disk = new ByteArrayOutputStream();
        try (InputStream inputStream = Files.newInputStream(CryptoPath.unwrap(path))) {
            IOUtils.copy(inputStream, disk);
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (InputStream inputStream = Channels.newInputStream(fileSystem.provider().newByteChannel(path, Collections.emptySet()))) {
            IOUtils.copy(inputStream, out);
        }

        // Then
        byte[] raw = disk.toByteArray();
        byte[] read = out.toByteArray();
        System.out.println(data.length + " - " + ByteArray.toHexString(data));
        System.out.println(raw.length + " - " + ByteArray.toHexString(raw));
        System.out.println(read.length + " - " + ByteArray.toHexString(read));
        Assert.assertTrue(Arrays.equals(data, read));
    }
}
