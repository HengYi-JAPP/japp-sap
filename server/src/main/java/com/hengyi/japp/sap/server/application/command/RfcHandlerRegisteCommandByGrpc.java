package com.hengyi.japp.sap.server.application.command;

import com.hengyi.japp.sap.DestinationType;
import com.hengyi.japp.sap.grpc.server.RegisteRfcHandlerRequest;
import com.hengyi.japp.sap.grpc.server.SapSysDef;
import com.sap.conn.jco.server.JCoServerFunctionHandler;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Min;

/**
 * Created by jzb on 17-7-11.
 */
public class RfcHandlerRegisteCommandByGrpc extends RfcHandlerRegisteCommand {
    @NotBlank
    private String clientHost;
    @Min(50000)
    private int clientPort;

    public RfcHandlerRegisteCommandByGrpc(RegisteRfcHandlerRequest request) {
        final SapSysDef sapSysDef = request.getSysDef();
        this.destinationType = DestinationType.valueOf(sapSysDef.getDestType().name());
        this.client = sapSysDef.getClient();
        this.user = sapSysDef.getUser();
        this.passwd = sapSysDef.getPasswd();

        this.progid = request.getProgid();
        this.clientHost = request.getClientHost();
        this.clientPort = request.getClientPort();
    }

    @Override
    public JCoServerFunctionHandler toJCoServerFunctionHandler() {
        //TODO 实现调用client 端的接口
        return null;
    }

    public String getClientHost() {
        return clientHost;
    }

    public void setClientHost(String clientHost) {
        this.clientHost = clientHost;
    }

    public int getClientPort() {
        return clientPort;
    }

    public void setClientPort(int clientPort) {
        this.clientPort = clientPort;
    }
}
