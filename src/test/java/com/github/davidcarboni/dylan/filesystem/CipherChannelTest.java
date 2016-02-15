package com.github.davidcarboni.dylan.filesystem;

import com.github.davidcarboni.cryptolite.Crypto;
import com.github.davidcarboni.cryptolite.Keys;
import com.github.davidcarboni.cryptolite.Random;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import static java.nio.file.StandardOpenOption.READ;
import static java.nio.file.StandardOpenOption.WRITE;
import static org.junit.Assert.*;

/**
 * Test for {@link CipherChannel}.
 */
public class CipherChannelTest {

    SecretKey key;
    Path path;
    SeekableByteChannel seekableByteChannel;

    @Before
    public void setUp() throws Exception {

        // Key
        key = Keys.newSecretKey();

        // New file
        path = Files.createTempFile(CipherChannelTest.class.getSimpleName(), ".test");

        // Open a channel that can be read or written
        seekableByteChannel = Files.newByteChannel(path, READ, WRITE);
    }

    @After
    public void tearDown() throws Exception {
        seekableByteChannel.close();
        Files.delete(path);
    }

    private void createContent(int size) throws IOException {

        try (CipherChannel cipherChannel = CipherChannel.wrap(Files.newByteChannel(path, WRITE), key)) {
            byte[] content = Random.bytes(size);
            ByteBuffer buffer = ByteBuffer.wrap(content);
            int count = 0;
            do {
                cipherChannel.write(buffer);
            } while (buffer.remaining() > 0);
        }
    }


    @Test(expected = IllegalStateException.class)
    public void shouldSetEncryptMode() throws IOException {

        // Given
        // A Cipher Channel
        CipherChannel cipherChannel = CipherChannel.wrap(seekableByteChannel, key);

        // When
        // We write to the channel
        cipherChannel.write(ByteBuffer.allocate(1));

        // Then
        // It should not be possible to read from the channel
        cipherChannel.read(ByteBuffer.allocate(1));
    }

    @Test(expected = IllegalStateException.class)
    public void shouldSetDecryptMode() throws IOException {

        // Given
        // A Cipher Channel
        CipherChannel cipherChannel = CipherChannel.wrap(seekableByteChannel, key);
        createContent(1);

        // When
        // We write to the channel
        cipherChannel.read(ByteBuffer.allocate(1));

        // Then
        // It should not be possible to read from the channel
        cipherChannel.write(ByteBuffer.allocate(1));
    }

    @Test
    public void shouldDiscountIvWhenCalculatingSize() throws IOException {

        for (int i = 0; i < 100; i++) {

            // Given
            // A Cipher Channel with no content
            createContent(i);
            CipherChannel cipherChannel = CipherChannel.wrap(Files.newByteChannel(path), key);

            // When
            // We ask for the size
            long size = cipherChannel.size();

            // Then
            // The channel size should exclude the iv
            assertEquals(i, size);
            // To confirm, the size of the file should be the CipherChannel size, plus the iv size:
            assertEquals(i + new Crypto().getIvSize(), Files.size(path));
        }
    }

    @Test
    public void shouldBeOpen() throws IOException {

        // Given
        // Uninitialised, read and write channels
        createContent(10);
        CipherChannel uninitialised = CipherChannel.wrap(Files.newByteChannel(path), key);
        CipherChannel write = CipherChannel.wrap(Files.newByteChannel(path, StandardOpenOption.WRITE), key);
        CipherChannel read = CipherChannel.wrap(Files.newByteChannel(path), key);
        write.write(ByteBuffer.allocate(1));
        read.read(ByteBuffer.allocate(1));


        // When
        // We ask if they are open
        boolean uninitialisedOpen = uninitialised.isOpen();
        boolean writeOpen = write.isOpen();
        boolean readOpen = read.isOpen();

        // Then
        // They should all be open
        assertTrue(uninitialisedOpen);
        assertTrue(writeOpen);
        assertTrue(readOpen);
    }

    @Test
    public void shouldClose() throws IOException {

        // Given
        // Uninitialised, read and write channels
        createContent(10);
        CipherChannel uninitialised = CipherChannel.wrap(Files.newByteChannel(path), key);
        CipherChannel write = CipherChannel.wrap(Files.newByteChannel(path, StandardOpenOption.WRITE), key);
        CipherChannel read = CipherChannel.wrap(Files.newByteChannel(path), key);
        write.write(ByteBuffer.allocate(1));
        read.read(ByteBuffer.allocate(1));


        // When
        // We ask if they are open
        uninitialised.close();
        write.close();
        read.close();

        // Then
        // They should all be open
        assertFalse(uninitialised.isOpen());
        assertFalse(write.isOpen());
        assertFalse(read.isOpen());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void shouldNotSeek() throws IOException {

        // Given
        // Uninitialised, read and write channels
        createContent(10);
        CipherChannel uninitialised = CipherChannel.wrap(Files.newByteChannel(path), key);
        CipherChannel write = CipherChannel.wrap(Files.newByteChannel(path, StandardOpenOption.WRITE), key);
        CipherChannel read = CipherChannel.wrap(Files.newByteChannel(path), key);
        write.write(ByteBuffer.allocate(1));
        read.read(ByteBuffer.allocate(1));


        // When
        // We try to set the position
        uninitialised.position(1);
        write.position(1);
        read.position(1);

        // Then
        // We should get an exception
    }

}