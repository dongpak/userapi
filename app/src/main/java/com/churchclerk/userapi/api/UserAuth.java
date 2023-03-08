/*

 */
package com.churchclerk.userapi.api;

import com.churchclerk.baseapi.BaseApi;
import com.churchclerk.userapi.model.User;
import com.churchclerk.userapi.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 */
@Component
@Path("/auth")
@Slf4j
public class UserAuth extends BaseApi<User> {

    @Autowired
    private UserService service;

    public UserAuth() {
        super(User.class);
    }

    @GET
    @Path("info")
    @Produces({MediaType.APPLICATION_JSON})
    public Response authInfo() {
        try {
            parseApiCallerInfo();
            return Response.ok(apiCaller).build();
        }
        catch (Throwable t) {
            return generateErrorResponse(t);
        }
    }

    @POST
    @Path("jwt")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Response authResource(User resource) {

        try {
            return Response.ok(service.authenticate(resource, getRemoteAddr())).build();
        }
        catch (Throwable t) {
            return generateErrorResponse(t);
        }
    }
}
