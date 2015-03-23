package edu.sjsu.cmpe273.lab2;

import io.grpc.ServerImpl;
import io.grpc.stub.StreamObserver;
import io.grpc.transport.netty.NettyServerBuilder;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

public class PollServer {

    private static final Logger logger = Logger.getLogger(PollServer.class.getName());

    /* Server port information */
    private int port = 50051;
    private ServerImpl server;
    private static int i = 100;

    private void start() throws Exception {
        server = NettyServerBuilder.forPort(port)
                .addService(PollServiceGrpc.bindService(new PollServiceImpl()))
                .build().start();
        logger.info("Server started, listening on : " + port );
        Runtime.getRuntime().addShutdownHook(new Thread(){
            @Override
            public void run(){
                System.err.println("*** shutting down gRPC server since JVM is shutting down");
                PollServer.this.stop();
                System.err.println("*** server shut down");
            }
        });

    }

    private void stop(){
        if(server != null){
            server.shutdown();
        }
    }

    /* Main method will launch the server */
    public static void main(String[] args) throws Exception{
        final PollServer server = new PollServer();
        server.start();
    }

    private class PollServiceImpl implements PollServiceGrpc.PollService{
        @Override
            public void createPoll(PollRequest req, StreamObserver<PollResponse> responseObserver){

            logger.info("client Request: Mod Id: " + req.getModeratorId());
            final AtomicInteger counter = new AtomicInteger(i++);

            String poll_id = converter(i);
            
            PollResponse reply = PollResponse.newBuilder().setId(poll_id).build();
            responseObserver.onValue(reply);
            responseObserver.onCompleted();

        }
        public String converter(int num)
            {
            String str="";
            while(num>0)
            {
            int t =0;
            t=num%36;
            if(t>9)
            {
                
                char t2=(char)(55+t);
                str=t2+str;
            }
            else
            {
                str=t+str;
            }
            num=num/36;
            }
            return str;
            }
    }

}

