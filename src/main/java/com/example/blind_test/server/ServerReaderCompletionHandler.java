package com.example.blind_test.server;

import com.example.blind_test.shared.communication.Request;
import com.example.blind_test.shared.gson_configuration.GsonConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.nio.channels.CompletionHandler;

public class ServerReaderCompletionHandler implements CompletionHandler<Integer, ByteBuffer> {

    private final static Logger logger = LoggerFactory.getLogger(ServerReaderCompletionHandler.class);

    ServerReaderCompletionHandler() {
        this.logger.info("ReaderCompletionHandler instantiated");
    }

    @Override
    public void completed(Integer result, ByteBuffer attachment) {
        logger.info("ReaderCompletionHandler completed with {}", result);
        String requestJson = new String(attachment.array()).substring(0, result);
        logger.info("{}", requestJson);
        Request requestObject = GsonConfiguration.gson.fromJson(requestJson, Request.class);
        ServerImpl.getFunctionWithRequestCode(requestObject).accept(requestObject.getRequestData());
    }

    @Override
    public void failed(Throwable exc, ByteBuffer attachment) {

    }
}
