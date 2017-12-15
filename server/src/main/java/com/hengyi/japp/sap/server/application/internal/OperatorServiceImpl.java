package com.hengyi.japp.sap.server.application.internal;

import com.hengyi.japp.sap.server.application.OperatorService;
import com.hengyi.japp.sap.server.application.command.OperatorCreateCommand;
import com.hengyi.japp.sap.server.application.command.OperatorUpdateCommand;
import com.hengyi.japp.sap.server.application.command.PasswordChangeCommand;
import com.hengyi.japp.sap.server.domain.Operator;
import com.hengyi.japp.sap.server.domain.repository.OperatorRepository;
import org.jzb.J;

import javax.ejb.Stateless;
import javax.inject.Inject;

/**
 * Created by jzb on 17-7-11.
 */
@Stateless
public class OperatorServiceImpl implements OperatorService {
    @Inject
    private OperatorRepository operatorRepository;

    @Override
    public Operator create(OperatorCreateCommand command) {
        Operator operator = new Operator();
        operator.setPassword(J.password(command.getPassword()));
        return save(operator, command);
    }

    private Operator save(Operator operator, OperatorUpdateCommand command) {
        operator.setName(command.getName());
        return operatorRepository.save(operator);
    }

    @Override
    public Operator update(String id, OperatorUpdateCommand command) {
        Operator operator = operatorRepository.find(id);
        return save(operator, command);
    }

    @Override
    public Operator update(String id, PasswordChangeCommand command) {
        if (!command.getNewPassword().equals(command.getReNewPassword())) {
            throw new RuntimeException();
        }
        return operatorRepository.find(id, command.getOldPassword())
                .map(operator -> {
                    operator.setPassword(J.password(command.getNewPassword()));
                    return operatorRepository.save(operator);
                })
                .orElseThrow(() -> new RuntimeException());
    }

}
