/*
 *
 */
package com.churchclerk.userapi.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.Objects;
import java.util.UUID;

/**
 *
 */
public class User extends BaseModel {

    private String  name;
    private String  token;
    private String roles;
    private UUID    churchId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonSerialize(using = TokenSerializer.class)
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }

    public UUID getChurchId() {
        return churchId;
    }

    public void setChurchId(UUID churchId) {
        this.churchId = churchId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        if (!super.equals(o)) return false;
        User user = (User) o;
        return Objects.equals(name, user.name) &&
                Objects.equals(token, user.token) &&
                Objects.equals(roles, user.roles) &&
                Objects.equals(churchId, user.churchId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), name, token, roles, churchId);
    }

    /**
     *
     * @param source
     */
    public void copy(User source) {
        super.copy(source);

        setChurchId(source.getChurchId());
        setName(source.getName());
        setRoles(source.getRoles());
        setToken(source.getToken());
    }
}
