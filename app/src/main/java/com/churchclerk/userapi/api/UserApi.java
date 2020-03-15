/**
 * 
 */
package com.churchclerk.userapi.api;

import com.churchclerk.baseapi.BaseApi;
import com.churchclerk.userapi.model.User;
import com.churchclerk.userapi.service.UserService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.*;


/**
 * 
 * @author dongp
 *
 */
@Component
@Path("/user")
public class UserApi extends BaseApi<User> {

	private static Logger logger = LoggerFactory.getLogger(UserApi.class);

	@QueryParam("name")
	private String nameLike;

	@QueryParam("token")
	private String tokenLike;

	@QueryParam("role")
	private String roleLike;

	@QueryParam("churchId")
	private String churchIdLike;


	@Autowired
	private UserService service;


	/**
	 * 
	 */
	public UserApi() {
		super(logger, User.class);
		setReadRoles(Role.ADMIN, Role.CLERK, Role.OFFICIAL, Role.MEMBER, Role.NONMEMBER);
		setUpdateRoles(Role.ADMIN);
		setDeleteRoles(Role.ADMIN);
	}

	@Override
	protected Page<? extends User> doGet(Pageable pageable) {

		return service.getResources(pageable, createCriteria());
	}

	private User createCriteria() {
		User criteria	= new User();

		criteria.setActive(true);
		if (active != null) {
			criteria.setActive(active.booleanValue());
		}
		criteria.setName(nameLike);
		criteria.setRoles(roleLike);

		if (readAllowed(churchIdLike == null ? null : churchIdLike)) {
			if (churchIdLike != null) {
				criteria.setChurchId(churchIdLike);
			}
		}
		else {
			// force return of empty array
			criteria.setChurchId(UUID.randomUUID().toString());
		}

		return criteria;
	}


	@Override
	protected User doGet(String id) {

		User resource = service.getResource(id);

		if ((resource == null) || (readAllowed(resource.getChurchId()) == false)) {
			throw new NotFoundException();
		}

		return resource;
	}

	@Override
	protected User doCreate(User resource) {

		if (createAllowed(resource.getChurchId())) {
			resource.setCreatedBy(requesterId);
			resource.setCreatedDate(new Date());
			resource.setUpdatedBy(requesterId);
			resource.setUpdatedDate(new Date());

			return service.createResource(resource);
		}

		throw new NotAuthorizedException("Invalid role");
	}

	@Override
	protected User doUpdate(User resource) {
		User found = service.getResource(id);

		if ((found == null) || (updateAllowed(found.getChurchId()) == false)) {
			throw new NotFoundException("User not found or not authorized");
		}

		if (resource.getName() == null) {
			resource.setName(found.getName());
		}
		if (resource.getRoles() == null) {
			resource.setRoles(found.getRoles());
		}
		if (resource.getChurchId() == null) {
			resource.setChurchId(found.getChurchId());
		}

		resource.setUpdatedBy(requesterId);
		resource.setUpdatedDate(new Date());

		return service.updateResource(resource);
	}

	@Override
	protected User doDelete(String id) {
		return service.deleteResource(id);
	}


}
