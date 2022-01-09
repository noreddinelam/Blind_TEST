package com.example.blind_test.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
public class ClientReaderCompletionHandler implements CompletionHandler<Integer, ByteBuffer> {
    private static Logger logger = LoggerFactory.getLogger(ClientReaderCompletionHandler.class);
    private final AsynchronousSocketChannel client;

    ClientReaderCompletionHandler(AsynchronousSocketChannel client){
        this.client = client;
    }
    @Override
    public void completed(Integer result, ByteBuffer attachment) {
        logger.info("{} chars have been read from the server", result);
        String jsonRes = new String(attachment.array()).substring(0,result);
        logger.info("Json object received from server {}",jsonRes);
    }

    @Override
    public void failed(Throwable exc, ByteBuffer attachment) {

    }
}
