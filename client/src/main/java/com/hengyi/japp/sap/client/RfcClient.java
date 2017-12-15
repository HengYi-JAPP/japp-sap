package com.hengyi.japp.sap.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hengyi.japp.sap.RfcExeCommand;
import com.hengyi.japp.sap.grpc.server.ExeRfcReply;

import static org.jzb.Constant.MAPPER;

public interface RfcClient {
    static String jsonString(Object o) {
        try {
            return MAPPER.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    ExeRfcReply exeRfc(String rfcName, RfcExeCommand command) throws Exception;

    void shutdown() throws Exception;
}
