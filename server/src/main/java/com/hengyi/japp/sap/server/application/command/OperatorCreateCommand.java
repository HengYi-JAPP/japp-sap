package com.hengyi.japp.sap.server.application.command;

import org.hibernate.validator.constraints.NotBlank;

/**
 * Created by jzb on 17-7-11.
 */
public class OperatorCreateCommand extends OperatorUpdateCommand {
    @NotBlank
    private String password;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
