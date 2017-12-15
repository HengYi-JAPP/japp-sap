package com.hengyi.japp.sap.server.interfaces.rest.resource;

import com.hengyi.japp.sap.ExeRfcResult;
import com.hengyi.japp.sap.RfcExeCommand;
import com.hengyi.japp.sap.server.application.SapService;
import org.hibernate.validator.constraints.NotBlank;
import org.jboss.resteasy.annotations.GZIP;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

/**
 * Created by jzb on 17-7-11.
 */
@GZIP
@Path("rfcs")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
public class RfcResource {
    @Inject
    private SapService sapService;

    @Path("{rfc}")
    @POST
    public ExeRfcResult create(@Context SecurityContext sc,
                               @Valid @NotBlank @PathParam("rfc") String rfc,
                               RfcExeCommand command) throws Exception {
        return sapService.exeRfc(sc.getUserPrincipal(), rfc, command);
    }

}
