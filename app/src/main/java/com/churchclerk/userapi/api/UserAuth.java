/*

 */
package com.churchclerk.userapi.api;

import com.churchclerk.baseapi.BaseApi;
import com.churchclerk.userapi.model.User;
import com.churchclerk.userapi.service.UserService;
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
public class UserAuth extends BaseApi<User> {

    private static Logger logger = LoggerFactory.getLogger(UserAuth.class);

    @Autowired
    private UserService service;

    public UserAuth() {
        super(logger, User.class);
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
