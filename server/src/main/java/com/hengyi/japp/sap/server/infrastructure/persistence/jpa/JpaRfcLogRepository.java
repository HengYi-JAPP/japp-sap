package com.hengyi.japp.sap.server.infrastructure.persistence.jpa;

import com.hengyi.japp.sap.server.domain.log.RfcLog;
import com.hengyi.japp.sap.server.domain.repository.RfcLogRepository;
import org.apache.commons.lang3.StringUtils;
import org.jzb.J;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;

/**
 * Created by jzb on 17-5-10.
 */
@ApplicationScoped
public class JpaRfcLogRepository implements RfcLogRepository {
    @Inject
    private EntityManager em;

    @Override
    public RfcLog save(RfcLog log) {
        if (StringUtils.isBlank(log.getId())) {
            log.setId(J.uuid58());
        }
        return em.merge(log);
    }

}