/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hengyi.japp.sap.server.interfaces.rest;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.jsonwebtoken.ExpiredJwtException;
import org.jzb.exception.JMultiThrowable;
import org.jzb.exception.JThrowable;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import static org.jzb.Constant.ErrorCode.SYSTEM;
import static org.jzb.Constant.ErrorCode.TOKEN_EXPIRED;
import static org.jzb.Constant.MAPPER;

/**
 * @author jzb
 */
@Provider
public class JappExceptionMapper implements ExceptionMapper<Throwable> {
    @Inject
    private Logger log;

    @Override
    public Response toResponse(Throwable ex) {
        Response.ResponseBuilder builder = Response.status(Status.FORBIDDEN);
        ArrayNode errors = MAPPER.createArrayNode();
        if (ex instanceof JThrowable) {
            JThrowable jex = (JThrowable) ex;
            errors.add(jex.toJsonNode());
        } else if (ex instanceof JMultiThrowable) {
            JMultiThrowable jex = (JMultiThrowable) ex;
            errors.addAll(jex.toJsonNode());
        } else if (ex instanceof ExpiredJwtException) {
            errors.add(MAPPER.createObjectNode().put("errorCode", TOKEN_EXPIRED));
        } else if (ex.getCause() != null) {
            return toResponse(ex.getCause());
        } else {
            log.error("", ex);
            ObjectNode error = MAPPER.createObjectNode()
                    .put("errorCode", SYSTEM)
                    .put("errorMsg", ex.getLocalizedMessage());
            errors.add(error);
        }
        return builder.entity(errors).build();
    }

}
