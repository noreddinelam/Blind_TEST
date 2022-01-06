package com.example.blind_test.server;

import com.example.blind_test.shared.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

public class Server {

    private static Logger logger = LoggerFactory.getLogger(Server.class);

    public static void main(String[] args) throws IOException {
        ServerImpl.initListOfFunctions();
        logger.info("Creating a server on port 9999");
        try (AsynchronousServerSocketChannel serverSocket = AsynchronousServerSocketChannel.open()) {
            serverSocket.bind(new InetSocketAddress(Properties.PORT));
            logger.info("Waiting a connection...");
            while (true) {
                serverSocket.accept(null, new CompletionHandler<AsynchronousSocketChannel, Object>() {
                    @Override
                    public void completed(AsynchronousSocketChannel result, Object attachment) {
                        try {
                            if (serverSocket.isOpen()) {
                                serverSocket.accept(null, this);
                            }
                            SocketAddress socketAddress = result.getRemoteAddress();
                            ServerImpl.addGuestClients(result);
                            logger.info("A client is connected from {}", socketAddress);
                            ByteBuffer buffer = ByteBuffer.allocate(Properties.BUFFER_SIZE);
                            logger.info("{} {}",result.getRemoteAddress(),result.getLocalAddress());
                            result.read(buffer, buffer, new ServerReaderCompletionHandler());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void failed(Throwable exc, Object attachment) {
                        exc.printStackTrace();
                    }
                });
                System.in.read();

            }
        }
    }
}

