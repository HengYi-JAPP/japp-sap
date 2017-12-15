package com.hengyi.japp.sap.server.application;

import com.hengyi.japp.sap.ExeRfcResult;
import com.hengyi.japp.sap.RfcExeCommand;
import com.hengyi.japp.sap.server.application.command.RfcHandlerRegisteCommand;

import java.security.Principal;

/**
 * Created by jzb on 17-7-11.
 */
public interface SapService {

    ExeRfcResult exeRfc(Principal principal, String rfcName, RfcExeCommand command) throws Exception;

    boolean registeRfcHandler(Principal principal, String fName, RfcHandlerRegisteCommand command) throws Exception;
}
