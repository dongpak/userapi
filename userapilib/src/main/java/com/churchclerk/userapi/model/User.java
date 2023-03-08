/*
 *
 */
package com.churchclerk.userapi.model;

import com.churchclerk.baseapi.model.BaseModel;
import com.churchclerk.memberapi.model.Member;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

/**
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
public class User extends BaseModel {

    private String  name;
    private String  token;
    private String  roles;
    private UUID    churchId;
    private Member  member;

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
