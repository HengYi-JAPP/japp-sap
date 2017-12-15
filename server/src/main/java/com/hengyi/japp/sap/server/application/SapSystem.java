package com.hengyi.japp.sap.server.application;

import com.hengyi.japp.sap.server.application.command.RfcHandlerRegisteCommand;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoException;

import java.security.Principal;

public interface SapSystem {
    JCoDestination getDestination() throws JCoException;

    boolean registeRfcHandler(Principal principal, String fName, RfcHandlerRegisteCommand command);
}
