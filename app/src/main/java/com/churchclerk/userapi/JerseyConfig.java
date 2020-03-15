/**
 * 
 */
package com.churchclerk.userapi;


import com.churchclerk.userapi.api.UserApi;
import com.churchclerk.userapi.api.UserAuth;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.stereotype.Component;

import javax.ws.rs.ApplicationPath;


@Component
@ApplicationPath("/api")
public class JerseyConfig extends ResourceConfig {
    public JerseyConfig() {
        registerEndpoints();
    }

    private void registerEndpoints() {
        register(UserApi.class);
        register(UserAuth.class);
    }
}