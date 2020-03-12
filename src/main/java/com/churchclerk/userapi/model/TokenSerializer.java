/*
 */
package com.churchclerk.userapi.model;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 *
 */
@Component
public class TokenSerializer extends JsonSerializer<String> {

    @Value("${spring.datasource.url}")
    private String		inTest;

    @Override
    public void serialize(String token, JsonGenerator gen, SerializerProvider provider) throws IOException, JsonProcessingException {
        if (inTest.startsWith("jdbc:h2:mem:testdb")) {
            gen.writeString(token);
        }
        else {
            gen.writeString("**********");
        }
    }
}
