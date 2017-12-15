package com.hengyi.japp.sap.client.internal;

import com.hengyi.japp.sap.RfcExeCommand;
import com.hengyi.japp.sap.client.RfcClient;
import com.hengyi.japp.sap.grpc.server.ExeRfcReply;
import com.hengyi.japp.sap.grpc.server.ExeRfcRequest;
import com.hengyi.japp.sap.grpc.server.SapServerGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static com.hengyi.japp.sap.Constant.GRPC_PORT;

public class RfcClientGrpcImpl implements RfcClient {
    private static RfcClientGrpcImpl INSTANCE;
    private final ManagedChannel channel;
    private final SapServerGrpc.SapServerBlockingStub blockingStub;

    private RfcClientGrpcImpl(ManagedChannel channel, SapServerGrpc.SapServerBlockingStub blockingStub) {
        this.channel = channel;
        this.blockingStub = blockingStub;
    }


    private RfcClientGrpcImpl(ManagedChannel channel) {
        this.channel = channel;
        blockingStub = SapServerGrpc.newBlockingStub(channel);
    }

    private RfcClientGrpcImpl(String host, int port) {
        this(ManagedChannelBuilder.forAddress(host, port)
                .maxInboundMessageSize(Integer.MAX_VALUE)
                .usePlaintext(true)
                .build()
        );
    }

    public static RfcClientGrpcImpl instance() {
        if (INSTANCE != null) {
            return INSTANCE;
        }
        return create();
    }

    private synchronized static RfcClientGrpcImpl create() {
        if (INSTANCE != null) {
            return INSTANCE;
        }
        INSTANCE = new RfcClientGrpcImpl("fp.hengyi.com", GRPC_PORT);
        // INSTANCE = new RfcClientGrpcImpl("192.168.0.97", GRPC_PORT);
//        INSTANCE = new RfcClientGrpcImpl("localhost", GRPC_PORT);
        return INSTANCE;
    }

    @Override
    public ExeRfcReply exeRfc(String rfcName, RfcExeCommand command) {
        final ExeRfcRequest.Builder builder = ExeRfcRequest.newBuilder().setRfcName(rfcName);
        Optional.ofNullable(command.getImports())
                .map(RfcClient::jsonString)
                .ifPresent(builder::setImports);
        Optional.ofNullable(command.getTables())
                .map(RfcClient::jsonString)
                .ifPresent(builder::setTables);
        Optional.ofNullable(command.getChangings())
                .map(RfcClient::jsonString)
                .ifPresent(builder::setChangings);
        return blockingStub.exeRfc(builder.build());
    }

    @Override
    public void shutdown() throws Exception {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }
}
