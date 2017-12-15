package com.hengyi.japp.sap.server.infrastructure.messaging.jms;

import com.hengyi.japp.sap.ExeRfcResult;
import com.hengyi.japp.sap.RfcExeCommand;
import com.hengyi.japp.sap.server.application.ApplicationEvents;
import com.hengyi.japp.sap.server.domain.log.RfcLog;
import org.jzb.J;
import org.slf4j.Logger;

import javax.annotation.Resource;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.jms.*;
import java.security.Principal;

import static com.hengyi.japp.sap.server.infrastructure.messaging.jms.JmsApplicationEvents.*;

/**
 * Created by jzb on 17-7-13.
 */
@JMSDestinationDefinitions({
        @JMSDestinationDefinition(
                name = QUEUE,
                resourceAdapter = "jmsra",
                interfaceName = "javax.jms.Queue",
                destinationName = QUEUE_DESTINATION),
        @JMSDestinationDefinition(
                name = TOPIC,
                resourceAdapter = "jmsra",
                interfaceName = "javax.jms.Topic",
                destinationName = TOPIC_DESTINATION),
})
@ApplicationScoped
public class JmsApplicationEvents implements ApplicationEvents {
    public static final String QUEUE_DESTINATION = "jappSapRest_Queue";
    public static final String QUEUE = "java:/jms/" + QUEUE_DESTINATION;
    public static final String TOPIC_DESTINATION = "jappSapRest_Topic";
    public static final String TOPIC = "java:/jms/" + TOPIC_DESTINATION;
    @Inject
    private Logger log;
    @Inject
    private JMSContext jmsContext;
    @Resource(mappedName = QUEUE)
    private Queue queue;
    @Resource(mappedName = TOPIC)
    private Topic topic;

    @Override
    public void rfcSuccessLog(Principal principal, long startTimeMillis, String rfcName, RfcExeCommand command, ExeRfcResult rfcResult) {
        try {
            MapMessage message = rfcLogMessage(principal, startTimeMillis, rfcName, command);
            message.setBoolean("success", true);
            message.setString("rfcResult", J.toJson(rfcResult));
            jmsContext.createProducer().send(topic, message);
        } catch (JMSException e) {
            log.error("", e);
        }
    }

    private MapMessage rfcLogMessage(Principal principal, long startTimeMillis, String rfcName, RfcExeCommand command) throws JMSException {
        long endTimeMillis = System.currentTimeMillis();
        MapMessage message = jmsContext.createMapMessage();
        message.setString("principal", principal.getName());
        message.setString("entityClass", RfcLog.class.getName());
        message.setLong("startTimeMillis", startTimeMillis);
        message.setLong("endTimeMillis", endTimeMillis);
        message.setString("destinationTypeName", command.getDestinationType().name());
        message.setString("client", command.getClient());
        message.setString("rfcName", rfcName);
        message.setString("command", J.toJson(command));
        return message;
    }

    @Override
    public void rfcFailLog(Principal principal, long startTimeMillis, String rfcName, RfcExeCommand command, Exception exception) {
        try {
            MapMessage message = rfcLogMessage(principal, startTimeMillis, rfcName, command);
            message.setBoolean("success", false);
            message.setString("execeptionMessage", exception.getMessage());
            jmsContext.createProducer().send(topic, message);
        } catch (JMSException e) {
            log.error("", e);
        }
    }
}
