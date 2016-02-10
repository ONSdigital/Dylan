package com.github.davidcarboni.dylan.filesystem;

import com.github.davidcarboni.cryptolite.Crypto;
import com.github.davidcarboni.cryptolite.Keys;
import com.github.davidcarboni.cryptolite.Random;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
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
 * Test for {@link DecryptingChannel}.
 */
public class DecryptingChannelTest {

    Path path;
    DecryptingChannel decryptingChannel;

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
        if (decryptingChannel != null)
            decryptingChannel.close();
        Files.delete(path);
    }

    private static Path createFile(SecretKey key, byte[] content) throws IOException {

        Path path = Files.createTempFile(DecryptingChannelTest.class.getSimpleName(), ".test");

        try (OutputStream outputStream = new Crypto().encrypt(Files.newOutputStream(path), key)) {
            outputStream.write(content);
        }

        return path;
    }

    @Test
    public void shouldDecrypt() throws Exception {

        ExecutorService pool = Executors.newFixedThreadPool(10);
        List<Future<Boolean>> futures = new ArrayList<>();
        for (int i = 0; i < 1024 * 1024 * 10; i += 250007) {
            final int length = i;
            Future<Boolean> future = pool.submit(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {

                    // Given
                    SecretKey key = Keys.newSecretKey();
                    byte[] content = Random.bytes(length);
                    Path path = createFile(key, content);

                    // When
                    try (DecryptingChannel decryptingChannel = DecryptingChannel.wrap(Files.newByteChannel(path), key)) {

                        // Then
                        byte[] read = new byte[content.length];
                        ByteBuffer buffer = ByteBuffer.allocate(997);
                        int count = 0;
                        while (count < read.length) {
                            int bytes = decryptingChannel.read(buffer);
                            buffer.flip();
                            byte[] data = new byte[buffer.limit()];
                            buffer.get(data);
                            System.arraycopy(data, 0, read, count, data.length);
                            buffer.clear();
                            count += bytes;
                        }
                        //Assert.assertTrue(Arrays.equals(content, read));
                        System.out.println((1024 * 1024 * 10) - length);
                        return Arrays.equals(content, read);
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
        decryptingChannel = DecryptingChannel.wrap(Files.newByteChannel(path), Keys.newSecretKey());

        // When
        boolean open = decryptingChannel.isOpen();

        // Then
        assertTrue(open);
    }

    @Test
    public void shouldClose() throws Exception {

        // Given
        decryptingChannel = DecryptingChannel.wrap(Files.newByteChannel(path), Keys.newSecretKey());

        // When
        decryptingChannel.close();

        // Then
        assertFalse(decryptingChannel.isOpen());
    }
}