package com.hengyi.japp.sap.server.application.internal;

import com.hengyi.japp.sap.ExeRfcResult;
import com.hengyi.japp.sap.RfcExeCommand;
import com.hengyi.japp.sap.grpc.server.*;
import com.hengyi.japp.sap.server.application.SapService;
import com.hengyi.japp.sap.server.application.command.RfcHandlerRegisteCommandByGrpc;
import com.hengyi.japp.sap.server.domain.repository.OperatorRepository;
import com.sun.security.auth.UserPrincipal;
import io.grpc.*;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import java.io.IOException;
import java.io.Serializable;
import java.security.Principal;

import static com.hengyi.japp.sap.Constant.GRPC_PORT;
import static io.grpc.Grpc.TRANSPORT_ATTR_REMOTE_ADDR;
import static org.jzb.Constant.MAPPER;

@Startup
@Singleton
public class GrpcServer extends SapServerGrpc.SapServerImplBase implements Serializable, ServerInterceptor {
    @Inject
    private Logger log;
    private Server server;
    @Inject
    private SapService sapService;
    @Inject
    private OperatorRepository operatorRepository;
    // TODO 暂时先用，以后加 ssl 验证
    private Principal principal = new UserPrincipal("LBMhKp5ML4qPVWZJJjGVKK");

    @Override
    public void exeRfc(ExeRfcRequest request, StreamObserver<ExeRfcReply> responseObserver) {
        try {
            RfcExeCommand command = new RfcExeCommand(request);
            ExeRfcResult res = sapService.exeRfc(principal, request.getRfcName(), command);
            ExeRfcReply reply = ExeRfcReply.newBuilder()
                    .setExports(MAPPER.writeValueAsString(res.getExports()))
                    .setChangings(MAPPER.writeValueAsString(res.getChangings()))
                    .setTables(MAPPER.writeValueAsString(res.getTables()))
                    .build();
            responseObserver.onNext(reply);
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("", e);
            responseObserver.onError(e);
        }
    }

    @Override
    public void registeRfcHandler(RegisteRfcHandlerRequest request, StreamObserver<RegisteRfcHandlerReply> responseObserver) {
        try {
            RfcHandlerRegisteCommandByGrpc command = new RfcHandlerRegisteCommandByGrpc(request);
            boolean success = sapService.registeRfcHandler(principal, request.getRfcName(), command);
            RegisteRfcHandlerReply reply = RegisteRfcHandlerReply.newBuilder()
                    .setSuccess(success)
                    .build();
            responseObserver.onNext(reply);
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("", e);
            responseObserver.onError(e);
        }
    }

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> call, Metadata headers, ServerCallHandler<ReqT, RespT> next) {
        System.out.println(call.getAttributes().get(TRANSPORT_ATTR_REMOTE_ADDR));
        return next.startCall(call, headers);
    }

    @PostConstruct
    void PostConstruct() {
        try {
            server = ServerBuilder.forPort(GRPC_PORT)
                    .addService(this)
                    .intercept(this)
                    .build()
                    .start();
            log.debug("sap server exeRfc grpc 启动成功，端口为：" + GRPC_PORT);
        } catch (IOException e) {
            log.error("sap server exeRfc grpc 启动失败！", e);
        }
    }

    @PreDestroy
    void PreDestroy() {
        if (server != null) {
            server.shutdown();
        }
    }
}
