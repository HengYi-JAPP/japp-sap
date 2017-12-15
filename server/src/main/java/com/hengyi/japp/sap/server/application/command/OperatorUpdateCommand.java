package com.hengyi.japp.sap.server.application.command;

import org.hibernate.validator.constraints.NotBlank;

import java.io.Serializable;

/**
 * Created by jzb on 17-7-11.
 */
public class OperatorUpdateCommand implements Serializable {
    @NotBlank
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
