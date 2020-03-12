/**
 * 
 */
package com.churchclerk.userapi;


import com.churchclerk.userapi.api.UserApi;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.stereotype.Component;

import javax.ws.rs.ApplicationPath;


@Component
@ApplicationPath("/api")
public class RestApiConfig extends ResourceConfig {
    public RestApiConfig() {
        registerEndpoints();
    }

    private void registerEndpoints() {
        register(UserApi.class);
    }
}