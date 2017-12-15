package com.hengyi.japp.sap.server;

import io.jsonwebtoken.impl.crypto.MacProvider;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.security.Key;
import java.util.Properties;

/**
 * @author jzb
 */
public class Constant {
    public static final String SAP_TRUE = "X";
    public static final String SAP_FALSE = "";

    public static final Properties CONFIG;
    public static final String SAP_DATAPROVIDER_PATH = CONFIG.getProperty("SAP_DATAPROVIDER_PATH");
    public static final Key JWT_KEY = MacProvider.generateKey();

    static {
        try {
            CONFIG = new Properties();
            CONFIG.load(new FileReader(new File(System.getProperty("SAP_CONFIG"))));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static final class ErrorCode {
        public static final String JCODESTINATION_NOT_FOUND = "E00004";
        public static final String JCOFUNCTION_NOT_FOUND = "E00005";
    }

}
