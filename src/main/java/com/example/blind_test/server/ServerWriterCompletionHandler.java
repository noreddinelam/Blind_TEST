package com.example.blind_test.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.nio.channels.CompletionHandler;

public class ServerWriterCompletionHandler implements CompletionHandler<Integer, ByteBuffer> {
    private static Logger logger = LoggerFactory.getLogger(ServerWriterCompletionHandler.class);

    @Override
    public void completed(Integer result, ByteBuffer attachment) {
        logger.info("{} chars have been sent to the client", result);
        logger.info("The capacity of the buffer {}", attachment.capacity());
    }

    @Override
    public void failed(Throwable exc, ByteBuffer attachment) {

    }
}
