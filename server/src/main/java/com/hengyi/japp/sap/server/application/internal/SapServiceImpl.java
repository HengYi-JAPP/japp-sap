package com.hengyi.japp.sap.server.application.internal;

import com.hengyi.japp.sap.ExeRfcResult;
import com.hengyi.japp.sap.RfcExeCommand;
import com.hengyi.japp.sap.server.Util;
import com.hengyi.japp.sap.server.application.SapService;
import com.hengyi.japp.sap.server.application.SapSystems;
import com.hengyi.japp.sap.server.application.command.RfcHandlerRegisteCommand;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoFunction;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.security.Principal;
import java.util.Map;

@Stateless
public class SapServiceImpl implements SapService {
    @Inject
    private SapSystems sapSystems;

    /**
     * 运行SAP的rfc
     *
     * @param principal 日志记录使用，
     * @param fName     rfc名字
     * @param command   rfc输入参数
     * @return
     */
    @Override
    public ExeRfcResult exeRfc(Principal principal, String fName, RfcExeCommand command) throws Exception {
        JCoDestination dest = sapSystems.find(command).getDestination();
        JCoFunction f = dest.getRepository().getFunctionTemplate(fName).getFunction();
        Util.setParam(f.getImportParameterList(), command.getImports());
        Util.setParam(f.getChangingParameterList(), command.getChangings());
        Util.setParam(f.getTableParameterList(), command.getTables());
        f.execute(dest);
        ExeRfcResult result = new ExeRfcResult();
        Map<String, Object> map = Util.toMap(f.getExportParameterList());
        result.setExports(map);
        map = Util.toMap(f.getChangingParameterList());
        result.setChangings(map);
        map = Util.toMap(f.getTableParameterList());
        result.setTables(map);
        return result;
    }

    @Override
    public boolean registeRfcHandler(Principal principal, String fName, RfcHandlerRegisteCommand command) throws Exception {
        return sapSystems.find(command).registeRfcHandler(principal, fName, command);
    }
}

