package com.hengyi.japp.sap.server.domain.log;

import com.hengyi.japp.sap.DestinationType;
import com.hengyi.japp.sap.server.domain.Operator;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * Created by jzb on 17-7-11.
 */
@Entity
@Table(name = "T_RFCLOG")
public class RfcLog extends AbstractLog {
    private boolean success;
    @Column(updatable = false)
    @Enumerated(EnumType.STRING)
    @NotNull
    private DestinationType destinationType;
    @Column(length = 20, updatable = false)
    @NotBlank
    private String client;
    @Column(updatable = false)
    @NotBlank
    private String rfcName;
    @Column(updatable = false)
    @Lob
    private String command;
    @Column(updatable = false)
    @Lob
    private String exeResult;

    public RfcLog(Operator operator) {
        this.operator = operator;
    }

    public RfcLog() {
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public DestinationType getDestinationType() {
        return destinationType;
    }

    public void setDestinationType(DestinationType destinationType) {
        this.destinationType = destinationType;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public String getRfcName() {
        return rfcName;
    }

    public void setRfcName(String rfcName) {
        this.rfcName = rfcName;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getExeResult() {
        return exeResult;
    }

    public void setExeResult(String exeResult) {
        this.exeResult = exeResult;
    }

}
