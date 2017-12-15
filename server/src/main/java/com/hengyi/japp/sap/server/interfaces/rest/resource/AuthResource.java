package com.hengyi.japp.sap.server.interfaces.rest.resource;

import com.fasterxml.jackson.databind.JsonNode;
import com.hengyi.japp.sap.server.Util;
import com.hengyi.japp.sap.server.domain.repository.OperatorRepository;
import org.hibernate.validator.constraints.NotBlank;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.jzb.Constant.MAPPER;

/**
 * Created by jzb on 17-7-11.
 */
@Path("auth")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
public class AuthResource {
    @Inject
    private OperatorRepository operatorRepository;

    @GET
    public Response create(@Valid @NotBlank @QueryParam("id") String id,
                           @Valid @NotBlank @QueryParam("password") String password) throws Exception {
        JsonNode result = operatorRepository.find(id, password)
                .map(Util::jwtToken)
                .map(token -> MAPPER.createObjectNode().put("token", token))
                .orElseThrow(() -> new RuntimeException());
        return Response.ok(result).build();
    }

}
