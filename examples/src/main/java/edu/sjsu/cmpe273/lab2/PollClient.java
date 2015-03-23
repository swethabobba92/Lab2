package edu.sjsu.cmpe273.lab2;

import io.grpc.ChannelImpl;
import io.grpc.transport.netty.NegotiationType;
import io.grpc.transport.netty.NettyChannelBuilder;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;


public class PollClient {

    private static final Logger logger = Logger.getLogger(PollClient.class.getName());

    private final ChannelImpl channel;
    private final PollServiceGrpc.PollServiceBlockingStub blockingStub;

    public PollClient(String host, int port){
        channel=
                NettyChannelBuilder.forAddress(host, port).negotiationType(NegotiationType.PLAINTEXT)
                .build();
        blockingStub = PollServiceGrpc.newBlockingStub(channel);
    }

    public void shutdown() throws InterruptedException{
        channel.shutdown().awaitTerminated(5,TimeUnit.SECONDS);
    }


    public void poll(){

        try{
            logger.info("Initiating Client messages....");
            PollRequest request = PollRequest.newBuilder()
                    .setModeratorId("12345")
                    .setQuestion("What type of Smart Phone do you have?")
                    .setStartedAt("2015-02-23T13:00:00.000Z")
                    .setExpiredAt("2015-02-24T13:00:00.000Z")
                    .addChoice("Android")
                    .addChoice("iPhone")
                    .build();
            PollResponse response = blockingStub.createPoll(request);
            logger.info("Server Response: Poll Id : " + response.getId());
        }catch(RuntimeException e){
            logger.log(Level.WARNING,"RPC Failed",e);
            return;
        }
    }


    public static void main(String[] args) throws Exception{
        PollClient client = new PollClient("localhost",50051);
        try{
            client.poll();
        }finally {
            client.shutdown();
        }
    }
}

