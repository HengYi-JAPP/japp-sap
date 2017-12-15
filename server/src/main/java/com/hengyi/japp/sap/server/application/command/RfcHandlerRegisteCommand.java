package com.hengyi.japp.sap.server.application.command;

import com.hengyi.japp.sap.SapSystemDefine;
import com.sap.conn.jco.server.JCoServerFunctionHandler;

/**
 * Created by jzb on 17-7-11.
 */
public abstract class RfcHandlerRegisteCommand extends SapSystemDefine {
    protected String progid;

    public String getProgid() {
        return progid;
    }

    public void setProgid(String progid) {
        this.progid = progid;
    }

    public abstract JCoServerFunctionHandler toJCoServerFunctionHandler();
}
