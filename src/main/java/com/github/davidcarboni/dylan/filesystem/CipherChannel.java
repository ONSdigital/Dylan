package com.github.davidcarboni.dylan.filesystem;

import com.github.davidcarboni.cryptolite.Crypto;
import org.apache.commons.lang3.ObjectUtils;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channel;
import java.nio.channels.SeekableByteChannel;

/**
 * Wrapper for either {@link DecryptingChannel} or {@link EncryptingChannel}.
 */
public class CipherChannel implements SeekableByteChannel {


    SeekableByteChannel channel;
    private SecretKey key;
    boolean initialised;
    EncryptingChannel encryptingChannel;
    DecryptingChannel decryptingChannel;

    public static CipherChannel wrap(SeekableByteChannel channel, SecretKey key) {
        return new CipherChannel(channel, key);
    }

    /**
     * The constructor is private to push callers towards the static wrap(...) method.
     *
     * @param channel The {@link SeekableByteChannel} to be wrapped.
     */
    private CipherChannel(SeekableByteChannel channel, SecretKey key) {
        this.channel = channel;
        this.key = key;
    }

    @Override
    public int read(ByteBuffer dst) throws IOException {
        initDecrypt();
        return decryptingChannel.read(dst);
    }

    @Override
    public int write(ByteBuffer src) throws IOException {
        initEncrypt();
        return encryptingChannel.write(src);
    }

    @Override
    public long position() throws IOException {
        // The position of the channel is the position of the underlying channel, minus the length of the iv
        int ivSize = getCrypto().getIvSize();
        return Math.max(0, channel.position() - ivSize);
    }

    @Override
    public SeekableByteChannel position(long newPosition) throws IOException {
        throw new UnsupportedOperationException("It is not safe to seek in a " + this.getClass().getSimpleName() +
                " because the state of the cipher would become out of sync with the channel position.");
    }

    @Override
    public long size() throws IOException {
        // The size of the data is the size of the underlying channel, minus the length of the iv
        int ivSize = getCrypto().getIvSize();
        return Math.max(0, channel.size() - ivSize);
    }

    @Override
    public SeekableByteChannel truncate(long size) throws IOException {
        return channel.truncate(size);
    }

    @Override
    public boolean isOpen() {
        return getChannel().isOpen();
    }

    @Override
    public void close() throws IOException {
        getChannel().close();
    }

    /**
     * @return Whichever of {@link EncryptingChannel} or {@link #decryptingChannel} has been initialised, as a {@link Channel}.
     * If neither has been initialised, the {@link #channel} field is returned.
     */
    Channel getChannel() {
        return ObjectUtils.defaultIfNull(
                ObjectUtils.defaultIfNull(encryptingChannel, decryptingChannel),
                channel);
    }

    /**
     * @return Whichever of {@link EncryptingChannel} or {@link #decryptingChannel} has been initialised, as a {@link Crypto}.
     * If neither has been initialised, a new {@link Crypto} instance is instantiated.
     */
    Crypto getCrypto() {
        return ObjectUtils.defaultIfNull(
                ObjectUtils.defaultIfNull(encryptingChannel, decryptingChannel),
                new Crypto());
    }

    /**
     * Initialises this channel for encryption by populating the {@link #encryptingChannel} field.
     *
     * @throws IOException If an error occurs in wrapping {@link #channel} with an {@link EncryptingChannel} instance.
     */
    void initEncrypt() throws IOException {

        if (decryptingChannel != null) {
            throw new IllegalStateException("This channel has already been initialised for decryption (read).");
        }

        if (!initialised) {
            encryptingChannel = EncryptingChannel.wrap(channel, key);
            initialised = true;
        }
    }


    /**
     * Initialises this channel for decryption by populating the {@link #decryptingChannel} field.
     *
     * @throws IOException If an error occurs in wrapping {@link #channel} with an {@link DecryptingChannel} instance.
     */
    void initDecrypt() throws IOException {

        if (encryptingChannel != null) {
            throw new IllegalStateException("This channel has already been initialised for encryption (write).");
        }

        if (!initialised) {
            decryptingChannel = DecryptingChannel.wrap(channel, key);
            initialised = true;
        }
    }
}
