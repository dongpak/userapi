/**
 * 
 */
package com.churchclerk.userapi.entity;

import com.churchclerk.userapi.model.User;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
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
