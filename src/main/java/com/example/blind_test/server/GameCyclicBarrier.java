package com.example.blind_test.server;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GameCyclicBarrier {
    private final ExecutorService executor;
    private final String response;
    private final AsynchronousSocketChannel[] clients;
    private final int nbPlayers;
    private ByteBuffer[] byteBuffers;
    private CyclicBarrier cyclicBarrier;
    private int currentByteBufferIndex = 0;

    public GameCyclicBarrier(String response, int nbPlayers, AsynchronousSocketChannel[] clients) {
        executor = Executors.newFixedThreadPool(nbPlayers);
        this.response = response;
        this.nbPlayers = nbPlayers;
        this.clients = clients;
        this.byteBuffers = new ByteBuffer[nbPlayers];
    }

    synchronized private void addByteBufferToByteBuffers() {
        ByteBuffer byteBuffer = ByteBuffer.wrap(response.getBytes());
        this.byteBuffers[currentByteBufferIndex++] = byteBuffer;
    }

    public void runBroadcast() {
        cyclicBarrier = new CyclicBarrier(this.nbPlayers, new ExecutorThread());
        for (int i = 0; i < this.nbPlayers; i++) {
            executor.execute(new ByteBufferThread());
        }
    }

    private class ByteBufferThread implements Runnable {
        @Override
        public void run() {
            try {
                addByteBufferToByteBuffers();
                cyclicBarrier.await();
            } catch (InterruptedException | BrokenBarrierException e) {
                e.printStackTrace();
            }
        }
    }

    private class ExecutorThread implements Runnable {
        @Override
        public void run() {
            for (int i = 0; i < nbPlayers; i++) {
                clients[i].write(byteBuffers[i], byteBuffers[i], new ServerWriterCompletionHandler());
            }
        }
    }

}
