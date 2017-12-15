package com.hengyi.japp.sap.server.infrastructure.messaging.jms;

import com.hengyi.japp.sap.DestinationType;
import com.hengyi.japp.sap.server.domain.Operator;
import com.hengyi.japp.sap.server.domain.log.RfcLog;
import com.hengyi.japp.sap.server.domain.repository.OperatorRepository;
import com.hengyi.japp.sap.server.domain.repository.RfcLogRepository;
import org.slf4j.Logger;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;
import java.util.Date;
import java.util.Objects;

/**
 * Created by jzb on 17-7-13.
 */
@MessageDriven(activationConfig = {
        @ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = JmsApplicationEvents.TOPIC),
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Topic"),
        @ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge")
})
public class RfcLog_mdb implements MessageListener {
    @Inject
    private Logger log;
    @Inject
    private OperatorRepository operatorRepository;
    @Inject
    private RfcLogRepository rfcLogRepository;

    @Override
    public void onMessage(Message inMessage) {
        try {
            MapMessage msg = (MapMessage) inMessage;
            String entityClass = msg.getString("entityClass");
            if (Objects.equals(entityClass, RfcLog.class.getName())) {
                rfcLog(msg);
            }
        } catch (Throwable e) {
            log.error(inMessage + "", e);
        }
    }

    private RfcLog rfcLog(MapMessage msg) throws JMSException {
        String principal = msg.getString("principal");
        Operator operator = operatorRepository.find(principal);
        String destinationTypeName = msg.getString("destinationTypeName");
        DestinationType destinationType = DestinationType.valueOf(destinationTypeName);
        String client = msg.getString("client");
        String rfcName = msg.getString("rfcName");
        String command = msg.getString("command");
        long startTimeMillis = msg.getLong("startTimeMillis");
        long endTimeMillis = msg.getLong("endTimeMillis");
        boolean success = msg.getBoolean("success");
        RfcLog log = new RfcLog(operator);
        log.setSuccess(success);
        log.setDestinationType(destinationType);
        log.setClient(client);
        log.setRfcName(rfcName);
        log.setCommand(command);
        log.setStartDateTime(new Date(startTimeMillis));
        log.setEndDateTime(new Date(endTimeMillis));
        if (success) {
            String rfcResult = msg.getString("rfcResult");
            log.setExeResult(rfcResult);
        } else {
            String execeptionMessage = msg.getString("execeptionMessage");
            log.setNote(execeptionMessage);
        }
        return rfcLogRepository.save(log);
    }

}
