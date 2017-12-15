package com.hengyi.japp.sap.server.application.decorator;

import com.hengyi.japp.sap.ExeRfcResult;
import com.hengyi.japp.sap.RfcExeCommand;
import com.hengyi.japp.sap.server.application.ApplicationEvents;
import com.hengyi.japp.sap.server.application.SapService;
import org.slf4j.Logger;

import javax.annotation.Resource;
import javax.decorator.Decorator;
import javax.decorator.Delegate;
import javax.ejb.Stateless;
import javax.enterprise.concurrent.ManagedScheduledExecutorService;
import javax.enterprise.inject.Any;
import javax.inject.Inject;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;
import java.security.Principal;

/**
 * Created by jzb on 17-7-11.
 */
@Stateless
@Decorator
public abstract class SapServiceLogDecorator implements SapService {
    @Inject
    @Delegate
    @Any
    private SapService sapService;
    @Inject
    private Logger log;
    @Resource
    private ManagedScheduledExecutorService ses;
    @Inject
    private ApplicationEvents applicationEvents;
    @Inject
    private UserTransaction userTransaction;

    @Override
    public ExeRfcResult exeRfc(Principal principal, String rfcName, RfcExeCommand command) throws Exception {
        long startTimeMillis = System.currentTimeMillis();
        try {
            ExeRfcResult result = sapService.exeRfc(principal, rfcName, command);
            applicationEvents.rfcSuccessLog(principal, startTimeMillis, rfcName, command, result);
            return result;
        } catch (Exception e) {
            ses.submit(() -> rfcFailLog(principal, startTimeMillis, rfcName, command, e));
            throw e;
        }
    }

    private void rfcFailLog(Principal principal, long startTimeMillis, String rfcName, RfcExeCommand command, Exception exception) {
        try {
            userTransaction.begin();
            applicationEvents.rfcFailLog(principal, startTimeMillis, rfcName, command, exception);
            userTransaction.commit();
        } catch (Exception e) {
            log.error("", e);
            try {
                userTransaction.rollback();
            } catch (SystemException e1) {
                log.error("", e1);
            }
        }
    }
}
