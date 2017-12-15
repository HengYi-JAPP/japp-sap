package com.hengyi.japp.sap.server.infrastructure.persistence.jpa;

import com.hengyi.japp.sap.server.Util;
import com.hengyi.japp.sap.server.domain.Operator;
import com.hengyi.japp.sap.server.domain.repository.OperatorRepository;
import io.jsonwebtoken.Claims;
import org.apache.commons.lang3.StringUtils;
import org.jzb.J;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.security.Principal;
import java.util.Optional;

/**
 * Created by jzb on 17-5-10.
 */
@ApplicationScoped
public class JpaOperatorRepository implements OperatorRepository {
    @Inject
    private EntityManager em;

    @Override
    public Operator save(Operator operator) {
        if (StringUtils.isBlank(operator.getId())) {
            operator.setId(J.uuid58());
        }
        return em.merge(operator);
    }

    @Override
    public Operator find(String id) {
        return em.find(Operator.class, id);
    }

    @Override
    public Operator find(Principal principal) {
        return find(principal.getName());
    }

    @Override
    public Operator findByJwt(String token) {
        Claims claims = Util.claims(token);
        return find(claims.getSubject());
    }

    @Override
    public Optional<Operator> find(String id, String password) {
        return Optional.ofNullable(find(id))
                .filter(operator -> {
                    String encryptPassword = operator.getPassword();
                    try {
                        return J.checkPassword(encryptPassword, password);
                    } catch (Exception e) {
                        return false;
                    }
                });
    }

}
