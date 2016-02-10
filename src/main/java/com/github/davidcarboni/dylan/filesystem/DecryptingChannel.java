package com.github.davidcarboni.dylan.filesystem;

import com.github.davidcarboni.cryptolite.Crypto;

import javax.crypto.*;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channel;
import java.nio.channels.ReadableByteChannel;

/**
 * A Decrypting {@link ReadableByteChannel} implementation.
 */
public class DecryptingChannel extends Crypto implements ReadableByteChannel {

    ReadableByteChannel channel;
    boolean finalised;

    public static DecryptingChannel wrap(ReadableByteChannel channel, SecretKey key) throws IOException {
        return new DecryptingChannel(channel, key);
    }

    /**
     * The constructor is private to push callers towards the static wrap(...) method.
     *
     * @param channel The {@link Channel} to be wrapped.
     * @throws IOException
     */
    private DecryptingChannel(ReadableByteChannel channel, SecretKey key) throws IOException {
        this.channel = channel;

        // Read the initalisation vector from the channel:
        int ivSize = getIvSize();
        ByteBuffer buffer = ByteBuffer.allocate(ivSize);
        byte[] iv = new byte[ivSize];
        int read = 0;
        do {
            // Attempt to read data:
            int count = channel.read(buffer);

            // Break the loop if we hit eof before the iv has been read:
            if (count < 1) {
                throw new IOException("Expected at least " + ivSize + " bytes for an initialisation vector (iv).");
            }

            // Transfer read bytes to the iv array:
            buffer.flip();
            while (buffer.hasRemaining()) {
                iv[read++] = buffer.get();
            }
        } while (read < ivSize);

        // Initialise the cipher instance with the IV:
        //System.out.println("Initialisation vector read: " + Arrays.toString(iv));
        initCipher(Cipher.DECRYPT_MODE, key, iv);
    }

    @Override
    public int read(ByteBuffer dst) throws IOException {

        // Work out a buffer capacity that will produce a decrypted output size
        // to fit the number of bytes remaining in the destination buffer:
        int capacity = dst.remaining();
        while (getCipher().getOutputSize(capacity) > dst.remaining()) capacity--;

        // Read encrypted data
        ByteBuffer ciphertext = ByteBuffer.allocate(capacity);
        int read = channel.read(ciphertext);
        ciphertext.flip();

        // Data read:
        if (read > 0) {
            try {
                return getCipher().update(ciphertext, dst);
            } catch (ShortBufferException e) {
                throw new IOException("Error reading encrypted data into buffer", e);
            }
        }

        // End of channel data:
        if (read == -1 && !finalised) {
            byte[] finalBytes;
            try {
                finalBytes = getCipher().doFinal();
            } catch (IllegalBlockSizeException | BadPaddingException e) {
                throw new IOException("Error finalising decryption process", e);
            }
            // TODO: we're assuming for now that the final bytes will fit into the given buffer:
            dst.put(finalBytes);
            finalised = true;
            return finalBytes.length;
        }

        // No bytes available, or end of data
        return read;
    }

    @Override
    public boolean isOpen() {
        return channel.isOpen();
    }

    @Override
    public void close() throws IOException {
        channel.close();
    }
}
