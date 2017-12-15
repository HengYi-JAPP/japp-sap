package com.hengyi.japp.sap.server.interfaces.rest;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

import static org.jzb.Constant.MAPPER;

/**
 * Created by jzb on 16-3-25.
 */
@Provider
public class JacksonContextResolver implements ContextResolver<ObjectMapper> {

    @Override
    public ObjectMapper getContext(Class<?> type) {
        return MAPPER;
    }

}
