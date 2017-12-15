package com.hengyi.japp.sap.server.exception;


import com.hengyi.japp.sap.DestinationType;
import org.jzb.exception.JException;

import static com.hengyi.japp.sap.server.Constant.ErrorCode.JCODESTINATION_NOT_FOUND;

/**
 * Created by jzb on 17-7-12.
 */
public class JCoDestinationNotFoundException extends JException {
    private final DestinationType destinationType;
    private final String client;

    public JCoDestinationNotFoundException(DestinationType destinationType, String client) {
        super(JCODESTINATION_NOT_FOUND, null);
        this.destinationType = destinationType;
        this.client = client;
    }

    public DestinationType getDestinationType() {
        return destinationType;
    }

    public String getClient() {
        return client;
    }

}
