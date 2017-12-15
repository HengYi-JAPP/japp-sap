package com.hengyi.japp.sap.server.application.internal;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.hengyi.japp.sap.DestinationType;
import com.hengyi.japp.sap.SapSystemDefine;
import com.hengyi.japp.sap.server.application.SapSystem;
import com.hengyi.japp.sap.server.application.SapSystems;
import com.hengyi.japp.sap.server.application.command.RfcHandlerRegisteCommand;
import com.sap.conn.jco.*;
import com.sap.conn.jco.ext.*;
import com.sap.conn.jco.server.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.slf4j.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.AccessTimeout;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.Principal;
import java.util.Collection;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import static com.hengyi.japp.sap.server.Constant.SAP_DATAPROVIDER_PATH;

@Startup
@Singleton
@AccessTimeout(value = 1, unit = TimeUnit.HOURS)
public class SapSystemsImpl implements SapSystems, DestinationDataProvider, ServerDataProvider {
    private static final String DELIMITER = "-";
    private final LoadingCache<DestinationType, Properties> cache = CacheBuilder.newBuilder()
            .maximumSize(3)
            .build(new CacheLoader<DestinationType, Properties>() {
                @Override
                public Properties load(DestinationType key) {
                    File file = FileUtils.getFile(SAP_DATAPROVIDER_PATH, key.name() + ".properties");
                    try (InputStream in = new FileInputStream(file)) {
                        Properties p = new Properties();
                        p.load(in);
                        return p;
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            });
    private Map<Triple<DestinationType, String, String>, SapSystemImpl> sapSystemMap = Maps.newConcurrentMap();
    private Collection<JCoServer> jCoServers = Lists.newArrayList();
    @Inject
    private Logger log;

    public static String destinationPropertiesKey(SapSystemDefine define) {
        String[] ss = new String[4];
        ss[0] = define.getDestinationType().name();
        ss[1] = define.getClient();
        ss[2] = define.getUser();
        ss[3] = define.getPasswd();
        return String.join(DELIMITER, ss);
    }

    public static String serverPropertiesKey(SapSystemDefine define, String progid) {
        return String.join(DELIMITER, destinationPropertiesKey(define), progid);
    }

//    private class SapSystemProgram implements JCoServerFunctionHandlerFactory {
//        private final String serverPropertiesName;
//        private final SapSystem sapSystem;
//        private final String progid;
//        private Map<String, JCoServerFunctionHandler> functionHandlerMap = Maps.newConcurrentMap();
//
//        private SapSystemProgram(SapSystem sapSystem, String progid) {
//            this.sapSystem = sapSystem;
//            this.progid = progid;
//            this.serverPropertiesName = J.uuid58(sapSystem.destinationPropertiesName, progid);
//        }
//
//        public boolean registeRfcHandler(String fName, RfcHandlerRegisteCommand command) throws JCoException {
//            if (functionHandlerMap.containsKey(fName)) {
//                return true;
//            }
//            String serverPropertiesName = J.uuid58(sapSystem.destinationPropertiesName, progid);
//            serverPropertiesMap.compute(serverPropertiesName, (id, properties) -> {
//                if (properties != null) {
//                    return properties;
//                }
//                Properties p = new Properties();
//                basePropertiesMap.get(sapSystem.destinationType).forEach((k, v) -> {
//                    p.setProperty((String) k, (String) v);
//                    p.setProperty(JCO_REPOSITORY_DEST, sapSystem.destinationPropertiesName);
//                    p.setProperty(JCO_PROGID, progid);
//                });
//                return p;
//            });
//            JCoServer server = JCoServerFactory.getServer(serverPropertiesName);
//            if (server.getState() != JCoServerState.ALIVE && server.getState() != JCoServerState.STARTED) {
//                server.start();
//            }
//            return true;
//        }
//
//        @Override
//        public JCoServerFunctionHandler getCallHandler(JCoServerContext jCoServerContext, String s) {
//            return null;
//        }
//
//        @Override
//        public void sessionClosed(JCoServerContext jCoServerContext, String s, boolean b) {
//
//        }
//    }

    @Override
    public SapSystem find(SapSystemDefine define) {
        Triple<DestinationType, String, String> triple = Triple.of(define.getDestinationType(), define.getClient(), define.getUser());
        return sapSystemMap.compute(triple, (k, v) -> {
            if (v == null) {
                return new SapSystemImpl(define);
            }
            v.setPasswd(define.getPasswd());
            return v;
        });
    }

    @PostConstruct
    void PostConstruct() {
        if (Environment.isDestinationDataProviderRegistered()) {
            return;
        }
        Environment.registerDestinationDataProvider(this);
        System.out.println("========注册SAP DESTINATION========");
        Environment.registerServerDataProvider(this);
        System.out.println("========注册SAP SERVER========");
    }

    @PreDestroy
    void PreDestroy() {
        if (!Environment.isDestinationDataProviderRegistered()) {
            return;
        }
        Environment.unregisterDestinationDataProvider(this);
        System.out.println("========取消注册SAP DESTINATION========");
        Environment.unregisterServerDataProvider(this);
        System.out.println("========取消注册SAP SERVER========");
        jCoServers.forEach(JCoServer::stop);
        System.out.println("========SAP SERVER停止========");
    }

    @Override
    public Properties getDestinationProperties(String s) {
        return destinationProperties(s);
    }

    /**
     * @param key SAP服务器加客户端加用户
     *            如：DEV-208-JZB-passwd
     *            如果不输入或者是null,那就是 PRO-800-RFC_JAPP-123456，生产机800
     */
    private Properties destinationProperties(String key) {
        try {
            String[] arr = key.split(DELIMITER);
            DestinationType destinationType = DestinationType.valueOf(arr[0]);
            Properties properties = new Properties();
            properties.putAll(cache.get(destinationType));
            String client = arr[1];
            properties.setProperty(JCO_CLIENT, client);
            String user = arr[2];
            properties.setProperty(JCO_USER, user);
            String passwd = arr[3];
            properties.setProperty(JCO_PASSWD, passwd);
            return properties;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Properties getServerProperties(String s) {
        return serverProperties(s);
    }

    /**
     * @param key SAP服务器加客户端加用户
     *            如：DEV-208-JZB-passwd-progid
     *            如果不输入或者是null,那就是 PRO-800-RFC_JAPP-123456-COM.HENGYI.JAPP，生产机800
     */
    private Properties serverProperties(String key) {
        try {
            String[] arr = key.split(DELIMITER);
            DestinationType destinationType = DestinationType.valueOf(arr[0]);
            Properties properties = new Properties();
            properties.putAll(cache.get(destinationType));
            String repDest = String.join(DELIMITER, destinationType.name(), arr[1], arr[2], arr[3]);
            properties.setProperty(JCO_REP_DEST, repDest);
            String progid = arr[4];
            properties.setProperty(JCO_PROGID, progid);
            return properties;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean supportsEvents() {
        return false;
    }

    @Override
    public void setServerDataEventListener(ServerDataEventListener serverDataEventListener) {

    }

    @Override
    public void setDestinationDataEventListener(DestinationDataEventListener destinationDataEventListener) {

    }

    public class SapSystemImpl implements SapSystem, JCoServerCallHandlerFactory, JCoServerFunctionHandler, JCoServerExceptionListener, JCoServerErrorListener, JCoServerStateChangedListener {
        private final SapSystemDefine define;
        private JCoServer jCoServer;
//        private final Map<String, SapSystemProgram> programMap = Maps.newConcurrentMap();

        private SapSystemImpl(SapSystemDefine define) {
            this.define = define;
        }

        private void setPasswd(String passwd) {
            this.define.setPasswd(passwd);
        }

        @Override
        public JCoDestination getDestination() throws JCoException {
            String key = destinationPropertiesKey(define);
            return JCoDestinationManager.getDestination(key);
        }

        @Override
        public boolean registeRfcHandler(Principal principal, String fName, RfcHandlerRegisteCommand command) {
            final String serverPropertiesKey = serverPropertiesKey(define, command.getProgid());
            getAndStartJcoServer(serverPropertiesKey);
            return jCoServer != null;
        }

        private synchronized JCoServer getAndStartJcoServer(String propertiesName) {
            if (jCoServer != null) {
                return jCoServer;
            }
            try {
                jCoServer = JCoServerFactory.getServer(propertiesName);
                jCoServer.setCallHandlerFactory(this);
                jCoServer.start();
                jCoServers.add(jCoServer);
                log.debug("========启动SAP SERVER========");
                return jCoServer;
            } catch (JCoException e) {
                log.error("========SAP SERVER启动失败========");
                throw new RuntimeException(e);
            }
        }

        @Override
        public void sessionClosed(JCoServerContext jCoServerContext, String s, boolean b) {

        }

        @Override
        public void handleRequest(JCoServerContext serverCtx, JCoFunction f) throws AbapException, AbapClassException {
            log.error("exeRfc[" + f.getName() + "]：没有 handler 为其执行！");
        }

        @Override
        public void serverStateChangeOccurred(JCoServer jCoServer, JCoServerState oldState, JCoServerState newState) {

        }

        @Override
        public void serverErrorOccurred(JCoServer jCoServer, String s, JCoServerContextInfo jCoServerContextInfo, Error error) {

        }

        @Override
        public void serverExceptionOccurred(JCoServer jCoServer, String s, JCoServerContextInfo jCoServerContextInfo, Exception e) {

        }
    }
}
