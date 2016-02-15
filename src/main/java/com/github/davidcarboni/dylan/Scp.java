//package com.github.davidcarboni.dylan;
//
//import org.apache.sshd.common.scp.ScpTransferEventListener;
//import org.apache.sshd.server.SshServer;
//import org.apache.sshd.server.auth.pubkey.PublickeyAuthenticator;
//import org.apache.sshd.server.command.ScpCommandFactory;
//import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
//import org.apache.sshd.server.session.ServerSession;
//
//import java.io.IOException;
//import java.nio.file.Path;
//import java.nio.file.attribute.PosixFilePermission;
//import java.security.PublicKey;
//import java.util.Set;
//
///**
// * Created by david on 26/01/2016.
// *
// * TODO: this needs to be updated from Ian's code.
// */
//public class Scp {
//
//    public static void main(String[] args) throws IOException, InterruptedException {
//
//        SshServer sshd = SshServer.setUpDefaultServer();
//        sshd.setKeyPairProvider(new SimpleGeneratorHostKeyProvider());
//        sshd.setPublickeyAuthenticator(new PublickeyAuthenticator() {
//            @Override
//            public boolean authenticate(String s, PublicKey publicKey, ServerSession serverSession) {
//                System.out.println("Authenticating user: " + s);
//                return true;
//            }
//        });
//        ScpCommandFactory scp = new ScpCommandFactory();
//        scp.addEventListener(new ScpTransferEventListener() {
//            @Override
//            public void startFileEvent(FileOperation fileOperation, Path path, long l, Set<PosixFilePermission> set) {
//                System.out.println("startFileEvent");
//                System.out.println(fileOperation == FileOperation.SEND ? "SEND" : "RECEIVE");
//                System.out.println(path);
//            }
//
//            @Override
//            public void endFileEvent(FileOperation fileOperation, Path path, long l, Set<PosixFilePermission> set, Throwable throwable) {
//                System.out.println("endFileEvent");
//                System.out.println(fileOperation == FileOperation.SEND ? "SEND" : "RECEIVE");
//                System.out.println(path);
//                System.out.println(throwable);
//            }
//
//            @Override
//            public void startFolderEvent(FileOperation fileOperation, Path path, Set<PosixFilePermission> set) {
//
//            }
//
//            @Override
//            public void endFolderEvent(FileOperation fileOperation, Path path, Set<PosixFilePermission> set, Throwable throwable) {
//
//            }
//        });
//        sshd.setCommandFactory(scp);
//        sshd.setPort(2323);
//        sshd.start();
//        do {
//            //System.out.println("tick..");
//            Thread.sleep(1000);
//        } while (true);
//    }
//}
