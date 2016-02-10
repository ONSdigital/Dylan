package com.github.davidcarboni.dylan.filesystem;

import com.github.davidcarboni.cryptolite.Crypto;
import com.github.davidcarboni.cryptolite.Keys;
import com.github.davidcarboni.cryptolite.Random;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Test for {@link EncryptingChannel}.
 */
public class EncryptingChannelTest {

    Path path;
    EncryptingChannel encryptingChannel;

    @Before
    public void setUp() throws Exception {

        // New file
        path = Files.createTempFile(DecryptingChannelTest.class.getSimpleName(), ".test");

        // Write an iv so we can open channels
        try (OutputStream outputStream = Files.newOutputStream(path)) {
            outputStream.write(Random.bytes(new Crypto().getIvSize()));
        }
    }

    @After
    public void tearDown() throws Exception {
        if (encryptingChannel != null)
            encryptingChannel.close();
        Files.delete(path);
    }

    private static Boolean checkFile(SecretKey key, byte[] content, Path path) throws IOException {

        byte[] decrypted = new byte[content.length];

        int moreContent;
        try (InputStream intputStream = new Crypto().decrypt(Files.newInputStream(path), key)) {
            IOUtils.readFully(intputStream, decrypted);
            moreContent = intputStream.read();
        }

        // Check there's no remaining content and that
        // what was read is the same as what was passed in:
        return moreContent == -1 && Arrays.equals(content, decrypted);
    }

    @Test
    public void shouldEncrypt() throws Exception {

        ExecutorService pool = Executors.newFixedThreadPool(10);
        List<Future<Boolean>> futures = new ArrayList<>();
        for (int i = 0; i < 1024 * 1024 * 10; i += 250007) {
            final int length = i;
            Future<Boolean> future = pool.submit(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {

                    // Given
                    SecretKey key = Keys.newSecretKey();
                    Path path = Files.createTempFile("should", "encrypt");

                    // When
                    try (EncryptingChannel encryptingChannel = EncryptingChannel.wrap(Files.newByteChannel(path, StandardOpenOption.WRITE), key)) {

                        // Then
                        byte[] content = Random.bytes(length);
                        int position = 0;
                        while (position < content.length) {
                            ByteBuffer buffer;
                            try {
                                buffer = ByteBuffer.wrap(content, position, Math.min(997, content.length - position));
                            } catch (IndexOutOfBoundsException e) {
                                throw new RuntimeException(e);
                            }
                            //buffer.flip();
                            int bytes = 0;
                            do {
                                bytes += encryptingChannel.write(buffer);
                            } while (buffer.remaining() > 0);
                            position += bytes;
                        }
                        //Assert.assertTrue(Arrays.equals(content, read));
                        System.out.println((1024 * 1024 * 10) - length);
                        return checkFile(key, content, path);
                    }
                }
            });
            futures.add(future);
        }

        for (Future<Boolean> future : futures) {
            assertTrue(future.get());
        }
    }

    @Test
    public void shouldBeOpen() throws Exception {

        // Given
        encryptingChannel = EncryptingChannel.wrap(Files.newByteChannel(path, StandardOpenOption.WRITE), Keys.newSecretKey());

        // When
        boolean open = encryptingChannel.isOpen();

        // Then
        assertTrue(open);
    }

    @Test
    public void shouldClose() throws Exception {

        // Given
        encryptingChannel = EncryptingChannel.wrap(Files.newByteChannel(path, StandardOpenOption.WRITE), Keys.newSecretKey());

        // When
        encryptingChannel.close();

        // Then
        assertFalse(encryptingChannel.isOpen());
    }
}