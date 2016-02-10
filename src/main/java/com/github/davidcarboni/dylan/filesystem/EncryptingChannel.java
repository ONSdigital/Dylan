package com.github.davidcarboni.dylan.filesystem;

import com.github.davidcarboni.cryptolite.Crypto;
import com.github.davidcarboni.cryptolite.Random;

import javax.crypto.*;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channel;
import java.nio.channels.WritableByteChannel;

/**
 * An encrypting {@link WritableByteChannel} implementation.
 */
public class EncryptingChannel extends Crypto implements WritableByteChannel {

    WritableByteChannel channel;
    boolean finalised;

    public static EncryptingChannel wrap(WritableByteChannel channel, SecretKey key) throws IOException {
        return new EncryptingChannel(channel, key);
    }

    /**
     * The constructor is private to push callers towards the static wrap(...) method.
     *
     * @param channel The {@link Channel} to be wrapped.
     * @throws IOException
     */
    private EncryptingChannel(WritableByteChannel channel, SecretKey key) throws IOException {
        this.channel = channel;

        // Read the initalisation vector from the channel:
        int ivSize = getIvSize();
        byte[] iv = Random.bytes(ivSize);
        ByteBuffer buffer = ByteBuffer.wrap(iv);

        // Write the iv
        int written = 0;
        do {
            written += channel.write(buffer);
        } while (written < ivSize);

        // Initialise the cipher instance with the iv:
        initCipher(Cipher.ENCRYPT_MODE, key, iv);
    }

    @Override
    public int write(ByteBuffer src) throws IOException {

        // Work out a buffer capacity that will hold the encrypted output size
        // of the bytes remaining in the source buffer:
        int capacity = getCipher().getOutputSize(src.remaining());

        // Read encrypted data
        ByteBuffer ciphertext = ByteBuffer.allocate(capacity);
        try {
            getCipher().update(src, ciphertext);
        } catch (ShortBufferException e) {
            throw new IOException("Error writing encrypted data to buffer", e);
        }
        ciphertext.flip();
        return writeFull(ciphertext);
    }

    @Override
    public boolean isOpen() {
        return channel.isOpen();
    }

    @Override
    public void close() throws IOException {

        // Finalise encryption
        if (!finalised) {
            try {
                // TODO: we're assuming the final data will be no more than one block in size:
                ByteBuffer finalBytes = ByteBuffer.allocate(getCipher().getBlockSize());
                getCipher().doFinal(ByteBuffer.allocate(0), finalBytes);
                finalBytes.flip();
                writeFull(finalBytes);
                finalised = true;
            } catch (ShortBufferException | IllegalBlockSizeException | BadPaddingException e) {
                throw new IOException("Error finalising encryption process", e);
            }
        }

        // Close the underlying channel
        channel.close();
    }

    /**
     * Fully writes the bytes remaining in the given {@link ByteBuffer} to the underlying channel.
     * This is needed because once the {@link Cipher} has been updated, we don't want to re-update the cipher with part of the same sequence.
     *
     * @param ciphertext The data to be fully written.
     * @return The number of bytes written. This should be the same as {@link ByteBuffer#remaining()} at the time this method was called.
     * @throws IOException If an error occurs in writing to {@link #channel}.
     */
    int writeFull(ByteBuffer ciphertext) throws IOException {
        int written = 0;
        while (ciphertext.remaining() > 0) {
            written += channel.write(ciphertext);
        }
        return written;
    }
}
