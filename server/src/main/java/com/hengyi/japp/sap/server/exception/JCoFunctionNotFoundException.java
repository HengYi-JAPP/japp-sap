package com.hengyi.japp.sap.server.exception;

import org.jzb.exception.JException;

import static com.hengyi.japp.sap.server.Constant.ErrorCode.JCOFUNCTION_NOT_FOUND;


/**
 * Created by jzb on 17-7-12.
 */
public class JCoFunctionNotFoundException extends JException {
    private final String fName;

    public JCoFunctionNotFoundException(String fName) {
        super(JCOFUNCTION_NOT_FOUND, null);
        this.fName = fName;
    }

    public String getfName() {
        return fName;
    }

}
