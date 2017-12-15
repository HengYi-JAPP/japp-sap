package com.hengyi.japp.sap.server.domain.repository;


import com.hengyi.japp.sap.server.domain.Operator;

import java.security.Principal;
import java.util.Optional;

/**
 * Created by jzb on 17-7-11.
 */
public interface OperatorRepository {
    Operator save(Operator operator);

    Optional<Operator> find(String id, String password);

    Operator find(String id);

    Operator find(Principal principal);

    Operator findByJwt(String token);

}
