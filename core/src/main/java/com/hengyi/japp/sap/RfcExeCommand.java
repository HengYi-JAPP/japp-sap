package com.hengyi.japp.sap;

import com.fasterxml.jackson.databind.JsonNode;
import com.hengyi.japp.sap.grpc.server.ExeRfcRequest;
import org.jzb.J;

import java.io.IOException;

import static org.jzb.Constant.MAPPER;

/**
 * Created by jzb on 17-7-11.
 */
public class RfcExeCommand extends SapSystemDefine {
    private JsonNode imports;
    private JsonNode changings;
    private JsonNode tables;

    public RfcExeCommand() {
    }

    public RfcExeCommand(ExeRfcRequest request) throws IOException {
        super(request.getSysDef());
        this.imports = toJsonNode(request.getImports());
        this.changings = toJsonNode(request.getChangings());
        this.tables = toJsonNode(request.getTables());
    }

    public JsonNode getImports() {
        return imports;
    }

    public void setImports(JsonNode imports) {
        this.imports = imports;
    }

    public void setImportsByJsonString(String jsonString) throws IOException {
        this.imports = toJsonNode(jsonString);
    }

    public JsonNode getChangings() {
        return changings;
    }

    public void setChangings(JsonNode changings) {
        this.changings = changings;
    }

    public void setChangingsByJsonString(String jsonString) throws IOException {
        this.changings = toJsonNode(jsonString);
    }

    public JsonNode getTables() {
        return tables;
    }

    public void setTables(JsonNode tables) {
        this.tables = tables;
    }

    public void setTablesByJsonString(String jsonString) throws IOException {
        this.tables = toJsonNode(jsonString);
    }

    private JsonNode toJsonNode(String s) throws IOException {
        return J.isBlank(s) ? null : MAPPER.readTree(s);
    }
}
