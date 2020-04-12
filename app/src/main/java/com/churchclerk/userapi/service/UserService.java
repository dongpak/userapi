/**
 * 
 */
package com.churchclerk.userapi.service;

import com.churchclerk.churchapi.entity.ChurchEntity;
import com.churchclerk.churchapi.model.Church;
import com.churchclerk.memberapi.entity.MemberEntity;
import com.churchclerk.memberapi.model.Member;
import com.churchclerk.securityapi.SecurityApi;
import com.churchclerk.securityapi.SecurityToken;
import com.churchclerk.userapi.model.User;
import com.churchclerk.userapi.entity.UserEntity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;

import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.NotFoundException;
import java.util.*;


/**
 * 
 * @author dongp
 *
 */
@Service
public class UserService {

	private static Logger logger	= LoggerFactory.getLogger(UserService.class);

	@Autowired
	private UserStorage storage;

	@Value("${jwt.secret}")
	private String		secret;

	/**
	 *
	 * @return
	 */
	public Page<? extends User> getResources(Pageable pageable, User criteria) {

		Page<UserEntity> page = storage.findAll(new UserResourceSpec(criteria), pageable);

		page.forEach(this::moveMember);
		return page;
	}

	private void moveMember(UserEntity entity) {
		if (entity.getMemberEntity() != null) {
			moveChurches(entity.getMemberEntity());
			entity.setMember(entity.getMemberEntity());
		}
	}

	private void moveChurches(MemberEntity entity) {
		if (entity.getChurchEntities() != null) {
			Set<Church> set = new HashSet<Church>();

			entity.getChurchEntities().forEach(set::add);
			entity.setChurches(set);
		}
	}

	/**
	 * 
	 * @param id
	 * @return
	 */
	public User getResource(String id) {

		Optional<UserEntity> optional = storage.findById(id);

		checkResourceNotFound(id, optional);

		UserEntity	entity = optional.get();

		moveMember(entity);
		return entity;
	}


	private void checkResourceNotFound(String id, Optional<UserEntity> optional) {
		if (optional.isPresent() == false) {
			throw new NotFoundException("No such User resource with id: " + id);
		}
	}

	/**
	 *
	 * @param resource
	 * @return
	 */
	public User createResource(User resource) {
		UserEntity entity = new UserEntity();

		encryptToken(resource);
		entity.copy(resource);

		if (resource.getMember() != null) {
			entity.setMemberEntity(
					createMemberEntity(resource.getMember())
			);
		}

		UserEntity saved = storage.save(entity);
		moveMember(saved);

		return saved;
	}

	private MemberEntity createMemberEntity(Member resource) {
		MemberEntity entity = new MemberEntity();

		entity.copy(resource);

		return entity;
	}


	private void encryptToken(User resource) {
		BCryptPasswordEncoder	pe	= new BCryptPasswordEncoder();

		resource.setToken(
				pe.encode(
						resource.getToken() == null ? "" : resource.getToken()
				)
		);
	}

	/**
	 *
	 * @param resource
	 * @return
	 */
	public User updateResource(User resource) {
		Optional<UserEntity> optional = storage.findById(resource.getName());

		checkResourceNotFound(resource.getName(), optional);

		UserEntity entity = optional.get();

		if ((resource.getToken() == null) || (resource.getToken().startsWith("*"))) {
			resource.setToken(entity.getToken());
		}
		else {
			encryptToken(resource);
		}

		entity.copyNonNulls(resource);
		if (resource.getMember() != null) {
			if ((entity.getMemberEntity() == null)
			||  (resource.getMember().getId().equals(entity.getMemberEntity().getId()) == false)) {
				// recreate member from resource
				entity.setMemberEntity(
						createMemberEntity(resource.getMember())
				);
			}
		}

		UserEntity saved = storage.save(entity);
		moveMember(saved);

		return saved;
	}


	/**
	 *
	 * @param id
	 * @return
	 */
	public User deleteResource(String id) {
		Optional<UserEntity> optional = storage.findById(id);

		checkResourceNotFound(id, optional);

		storage.deleteById(id);

		UserEntity entity = optional.get();
		moveMember(entity);

		return entity;
	}

	/**
	 *
	 * @param resource
	 * @return
	 */
	public String authenticate(User resource, String location) {
		Optional<UserEntity> optional = storage.findById(resource.getName());

		if (optional.isPresent() == false) {
			throw new NotAuthorizedException("Invalid credentials");
		}

		BCryptPasswordEncoder	pe	= new BCryptPasswordEncoder();

		if (pe.matches(resource.getToken(), optional.get().getToken()) == false) {
			throw new NotAuthorizedException("Invalid credentials");
		}

		SecurityToken	token = new SecurityToken();

		token.setId(createIdentifiers(optional.get()));
		token.setRoles(optional.get().getRoles());
		token.setLocation(location);
		token.setSecret(secret);
		token.setValidFor(1000*60*60*24);

		if (SecurityApi.process(token) == false) {
			throw new NotAuthorizedException("Invalid configuration");
		}

		return token.getJwt();
	}

	private String createIdentifiers(UserEntity entity) {
		StringBuffer	buffer = new StringBuffer();

		buffer.append(entity.getName());
		buffer.append("|");

		if (entity.getMemberEntity() != null) {
			buffer.append(entity.getMemberEntity().getId());

			if (entity.getMemberEntity().getChurchEntities() != null) {
				entity.getMemberEntity().getChurchEntities().forEach(church -> {
					buffer.append("|");
					buffer.append(church.getId());
				});
			}
		}

		return buffer.toString();
	}
}
