package com.hengyi.japp.sap.hsf.api;

import com.hengyi.japp.sap.RfcExeCommand;
import com.hengyi.japp.sap.grpc.server.ExeRfcReply;

/**
 * 描述：Sap 调用接口
 *
 * @author jzb 2017-12-23
 */
public interface SapService {
    ExeRfcReply exeRfc(String rfcName, RfcExeCommand command) throws Exception;
}
