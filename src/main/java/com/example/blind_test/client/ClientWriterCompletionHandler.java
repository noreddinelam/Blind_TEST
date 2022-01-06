package com.example.blind_test.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.nio.channels.CompletionHandler;

public class ClientWriterCompletionHandler implements CompletionHandler<Integer, ByteBuffer> {
    private static Logger logger = LoggerFactory.getLogger(ClientWriterCompletionHandler.class);

    @Override
    public void completed(Integer result, ByteBuffer attachment) {
        logger.info("{} chars have been sent to the server", result);
        String dataSent = new String(attachment.array());
        logger.info("Data sent to the server \n{}",dataSent);
    }

    @Override
    public void failed(Throwable exc, ByteBuffer attachment) {

    }
}
