/**
 * 
 */
package com.churchclerk.userapi.service;

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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

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

		return page;
	}

	/**
	 * 
	 * @param id
	 * @return
	 */
	public User getResource(String id) {

		Optional<UserEntity> entity = storage.findById(id);
		return entity.get();
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

		return storage.save(entity);
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

		if (optional.isPresent()) {
			UserEntity entity = optional.get();

			if ((resource.getToken() == null) || (resource.getToken().startsWith("*"))) {
				resource.setToken(entity.getToken());
			}
			else {
				encryptToken(resource);
			}

			entity.copy(resource);
			return storage.save(entity);
		}

		return resource;
	}


	/**
	 *
	 * @param id
	 * @return
	 */
	public User deleteResource(String id) {
		Optional<UserEntity> optional = storage.findById(id);

		if (optional.isPresent() == false) {
			throw new NotFoundException("No such resource with id: " + id);
		}

		storage.deleteById(id);
		return optional.get();
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

		token.setId(resource.getName() + "|" + optional.get().getChurchId());
		token.setRoles(optional.get().getRoles());
		token.setLocation(location);
		token.setSecret(secret);
		token.setValidFor(10000000);

		if (SecurityApi.process(token) == false) {
			throw new NotAuthorizedException("Invalid configuration");
		}

		return token.getJwt();
	}
}
