package com.hengyi.japp.sap.hsf.api.impl;

import com.hengyi.japp.sap.RfcExeCommand;
import com.hengyi.japp.sap.client.JSap;
import com.hengyi.japp.sap.client.RfcClient;
import com.hengyi.japp.sap.grpc.server.ExeRfcReply;
import com.hengyi.japp.sap.hsf.api.SapService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * 描述： Sap 调用实现
 *
 * @author jzb 2017-12-23
 */
@Service(value = "sapService")
public class SapServiceImpl implements SapService {
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final RfcClient rfcClient = JSap.grpcClient();

    @PostConstruct
    void PostConstruct() {
        log.debug("test");
    }

    @Override
    public ExeRfcReply exeRfc(String rfcName, RfcExeCommand command) throws Exception {
        return rfcClient.exeRfc(rfcName, command);
    }
}
