/**
 * 
 */
package com.churchclerk.userapi.entity;

import com.churchclerk.memberapi.entity.MemberEntity;
import com.churchclerk.memberapi.model.Member;
import com.churchclerk.userapi.model.User;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;


/**
 * 
 * @author dongp
 *
 */
@Entity
@Table(name="user")
public class UserEntity extends User {

	private MemberEntity memberEntity;

	@Column(name="active")
	@Override
	public boolean isActive() {
		return super.isActive();
	}

	@Id
	@Column(name="name")
	@Override
	public String getName() {
		return super.getName();
	}

	@Column(name="token")
	@Override
	public String getToken() {
		return super.getToken();
	}

	@Column(name="roles")
	@Override
	public String getRoles() {
		return super.getRoles();
	}

	@Column(name="church_id")
	public String getChurchId() {
		return super.getChurchId();
	}

	@JsonIgnore
	@OneToOne(fetch = FetchType.EAGER, optional = true, cascade = {CascadeType.PERSIST})
	@JoinColumn(name = "member_id", nullable = true)
	public MemberEntity getMemberEntity() {
		return memberEntity;
	}

	public void setMemberEntity(MemberEntity memberEntity) {
		this.memberEntity = memberEntity;
	}

	@Transient
	public Member getMember() {
		return super.getMember();
	}

	@Override
	public Date getCreatedDate() {
		return super.getCreatedDate();
	}

	@Override
	public String getCreatedBy() {
		return super.getCreatedBy();
	}

	@Override
	public Date getUpdatedDate() {
		return super.getUpdatedDate();
	}

	@Override
	public String getUpdatedBy() {
		return super.getUpdatedBy();
	}
}
