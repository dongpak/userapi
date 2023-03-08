/**
 * 
 */
package com.churchclerk.userapi.entity;

import com.churchclerk.memberapi.entity.MemberEntity;
import com.churchclerk.memberapi.model.Member;
import com.churchclerk.userapi.model.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

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
@SuperBuilder
@NoArgsConstructor
@Setter
@EntityListeners(AuditingEntityListener.class)
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
	public UUID getChurchId() {
		return super.getChurchId();
	}

	@JsonIgnore
	@OneToOne(fetch = FetchType.EAGER, optional = true, cascade = {CascadeType.PERSIST})
	@JoinColumn(name = "member_id", nullable = true)
	public MemberEntity getMemberEntity() {
		return memberEntity;
	}

	@Transient
	public Member getMember() {
		return super.getMember();
	}

	@Column(name="created_date")
	@Override
	public Date getCreatedDate() {
		return super.getCreatedDate();
	}

	@Column(name="created_by")
	@Override
	public String getCreatedBy() {
		return super.getCreatedBy();
	}

	@Column(name="updated_date")
	@Override
	public Date getUpdatedDate() {
		return super.getUpdatedDate();
	}

	@Column(name="updated_by")
	@Override
	public String getUpdatedBy() {
		return super.getUpdatedBy();
	}
}
