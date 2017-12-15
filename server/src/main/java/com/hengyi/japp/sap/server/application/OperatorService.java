package com.hengyi.japp.sap.server.application;

import com.hengyi.japp.sap.server.application.command.OperatorCreateCommand;
import com.hengyi.japp.sap.server.application.command.OperatorUpdateCommand;
import com.hengyi.japp.sap.server.application.command.PasswordChangeCommand;
import com.hengyi.japp.sap.server.domain.Operator;

/**
 * Created by jzb on 17-7-11.
 */
public interface OperatorService {
    Operator create(OperatorCreateCommand command);

    Operator update(String id, OperatorUpdateCommand command);

    Operator update(String id, PasswordChangeCommand command);
}
