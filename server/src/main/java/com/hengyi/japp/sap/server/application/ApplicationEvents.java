package com.hengyi.japp.sap.server.application;

import com.hengyi.japp.sap.ExeRfcResult;
import com.hengyi.japp.sap.RfcExeCommand;

import java.security.Principal;

/**
 * Created by jzb on 17-7-13.
 */
public interface ApplicationEvents {

    void rfcSuccessLog(Principal principal, long startTimeMillis, String rfcName, RfcExeCommand command, ExeRfcResult rfcResult);

    void rfcFailLog(Principal principal, long startTimeMillis, String rfcName, RfcExeCommand command, Exception e);
}
