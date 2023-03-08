/**
 * 
 */
package com.churchclerk.userapi.api;

import com.churchclerk.baseapi.BaseApi;
import com.churchclerk.baseapi.model.ApiCaller;
import com.churchclerk.userapi.model.User;
import com.churchclerk.userapi.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import java.util.Date;
import java.util.UUID;


/**
 * 
 * @author dongp
 *
 */
@Component
@Path("/user")
@Slf4j
public class UserApi extends BaseApi<User> {

	@QueryParam("name")
	private String nameLike;

	@QueryParam("roles")
	private String rolesLike;

	@QueryParam("churchId")
	private String churchId;


	@Autowired
	private UserService service;


	/**
	 * 
	 */
	public UserApi() {
		super(User.class);
		setCreateRoles(ApiCaller.Role.ADMIN);
		setReadRoles(ApiCaller.Role.ADMIN);
		setUpdateRoles(ApiCaller.Role.ADMIN);
		setDeleteRoles(ApiCaller.Role.ADMIN);
	}

	@Override
	protected Page<? extends User> doGet(Pageable pageable) {

		return service.getResources(pageable, createCriteria());
	}

	private User createCriteria() {
		User criteria	= new User();

		addBaseCriteria(criteria);

		criteria.setName(nameLike);
		criteria.setRoles(rolesLike);

		if (churchId == null) {
			apiCaller.getMemberOf().forEach(id -> {
				if (churchId == null) {
					churchId = id;
				}
			});
		}

		if (readAllowed(churchId)) {
			if (churchId != null) {
				criteria.setChurchId(UUID.fromString(churchId));
			}
		}
		else {
			// force return of empty array
			criteria.setChurchId(null);
		}

		return criteria;
	}


	@Override
	protected User doGet() {
		if ((id == null) || (id.trim().isEmpty())) {
			throw new BadRequestException("User id/name cannot be empty");
		}

		User resource = service.getResource(id);

		if ((resource == null) || (readAllowed(resource.getChurchId().toString()) == false)) {
			throw new NotFoundException();
		}

		return resource;
	}

	@Override
	protected User doCreate(User resource) {
		if (resource.getId() != null) {
			throw new BadRequestException("User id should not be present");
		}

		if ((resource.getName() == null) || (resource.getToken() == null) || (resource.getRoles() == null) || (resource.getChurchId() == null)) {
			throw new BadRequestException("User's name, token, roles, and churchId cannot be null");
		}

		if (createAllowed(resource.getChurchId().toString()) == false) {
			throw new ForbiddenException();
		}

		return service.createResource(resource);
	}

	@Override
	protected User doUpdate(User resource) {
		if ((id == null) || (id.isEmpty()) || (resource.getName() == null) || (resource.getName().isEmpty())) {
			throw new BadRequestException("User id/name cannot be empty");
		}

		if (resource.getName().equals(id) == false) {
			throw new BadRequestException("User id/name does not match");
		}

		if ((resource.getChurchId() == null) && (hasSuperRole() == false)) {
			throw new BadRequestException("User's churchId  cannot be empty");
		}

		User found = service.getResource(id);

		if (found == null) {
			throw new NotFoundException("User not found: " + id);
		}

		// -- church id is null and has super role, then allow
		if (updateAllowed(found.getChurchId().toString(), this::hasSuperRole) == false) {
			throw new ForbiddenException();
		}

//		resource.setUpdatedBy(apiCaller.getUserid());
//		resource.setUpdatedDate(new Date());

		return service.updateResource(resource);
	}

	@Override
	protected User doDelete() {
		if ((id == null) || id.isEmpty()) {
			throw new BadRequestException("User id cannot be empty");
		}

		User found = service.getResource(id);

		if (found == null) {
			throw new NotFoundException("User not found: " + id);
		}

		if (found.getChurchId() == null) {
			throw new ForbiddenException();
		}

		if (found.getName().equals(apiCaller.getUserid())) {
			throw new ForbiddenException();
		}

		if (deleteAllowed(found.getChurchId().toString()) == false) {
			throw new ForbiddenException();
		}

		return service.deleteResource(id);
	}
}
