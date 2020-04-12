/*
 *
 */
package com.churchclerk.userapi.model;

import com.churchclerk.baseapi.model.BaseModel;
import com.churchclerk.memberapi.model.Member;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.Objects;

/**
 *
 */
public class User extends BaseModel {

    private String  name;
    private String  token;
    private String  roles;
    private String  churchId;
    private Member  member;

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

    public String getChurchId() {
        return churchId;
    }

    public void setChurchId(String churchId) {
        this.churchId = churchId;
    }

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
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
                Objects.equals(churchId, user.churchId) &&
                Objects.equals(member, user.member);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), name, token, roles, churchId, member);
    }

    /**
     *
     * @param source
     */
    public void copy(User source) {
        super.copy(source);

        setChurchId(source.getChurchId());
        setMember(source.getMember());
        setName(source.getName());
        setRoles(source.getRoles());
        setToken(source.getToken());
    }

    /**
     *
     * @param source
     */
    public void copyNonNulls(User source) {
        super.copyNonNulls(source);
        copy(source.getChurchId(), this::setChurchId);
        copy(source.getMember(), this::setMember);
        copy(source.getName(), this::setName);
        copy(source.getRoles(), this::setRoles);
        copy(source.getToken(), this::setToken);
    }
}
