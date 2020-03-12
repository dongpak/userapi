/**
 * 
 */
package com.churchclerk.userapi.api;

import com.churchclerk.userapi.model.User;
import com.churchclerk.userapi.service.UserService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
public class UserApi extends BaseApi {

	private static Logger logger = LoggerFactory.getLogger(UserApi.class);
	
	
	@PathParam("id")
	private String id;

	@DefaultValue("0")
	@QueryParam("page")
	private int page = 0;

	@DefaultValue("20")
	@QueryParam("size")
	private int size = 20;

	@QueryParam("name")
	private String nameLike;

	@QueryParam("token")
	private String tokenLike;

	@QueryParam("role")
	private String roleLike;

	@QueryParam("churchId")
	private String churchIdLike;

	@QueryParam("active")
	private Boolean active;

	@QueryParam("sortBy")
	private String sortBy;

	@Autowired
	private UserService service;


	/**
	 * 
	 */
	public UserApi() {
		super(logger);
		setReadRoles(Role.ADMIN, Role.CLERK, Role.OFFICIAL, Role.MEMBER, Role.NONMEMbER);
		setUpdateRoles(Role.ADMIN);
		setDeleteRoles(Role.ADMIN);
	}


	@GET
	@Produces({MediaType.APPLICATION_JSON})

	public Response getResources() {
		try {
			verifyToken();
			Pageable pageable = PageRequest.of(page, size, createSort());

			return Response.ok(service.getResources(pageable, createCriteria())).build();
		}
		catch (Throwable t) {
			return generateErrorResponse(t);
		}
	}

	private User createCriteria() {
		User criteria	= new User();

		criteria.setActive(true);
		if (active != null) {
			criteria.setActive(active.booleanValue());
		}
		criteria.setName(nameLike);
		criteria.setRoles(roleLike);

		if (readAllowed(churchIdLike == null ? null : UUID.fromString(churchIdLike))) {
			if (churchIdLike != null) {
				criteria.setChurchId(UUID.fromString(churchIdLike));
			}
		}
		else {
			criteria.setChurchId(UUID.randomUUID());
		}

		return criteria;
	}

	private Sort createSort() {
		List<Sort.Order> list = new ArrayList<Sort.Order>();

		if (sortBy != null) {
			for (String item : sortBy.split(",")) {
				Sort.Direction 	dir 	= Sort.Direction.ASC;
				String			field	= item;

				if (item.startsWith("-")) {
					dir		= Sort.Direction.DESC;
					field 	= item.substring(1);
				}
				else if (item.startsWith("+")) {
					field 	= item.substring(1);
				}

				list.add(new Sort.Order(dir, field));
			}
		}

		return Sort.by(list);
	}

	@GET
	@Path("{id}")
	@Produces({MediaType.APPLICATION_JSON})
	public Response getResource() {
		try {
			verifyToken();

			User	resource = service.getResource(id);

			if (readAllowed(resource.getChurchId())) {
				return Response.ok(resource).build();
			}
			else {
				throw new NotFoundException();
			}
		}
		catch (Throwable t) {
			return generateErrorResponse(t);
		}
	}

	@POST
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public Response createResource(User resource) {
		
		try {
			verifyToken();
			if (createAllowed(resource.getChurchId())) {
				resource.setActive(true);
				resource.setCreatedBy(requesterId);
				resource.setCreatedDate(new Date());
				resource.setUpdatedBy(requesterId);
				resource.setUpdatedDate(new Date());

				User newResource = service.createResource(resource);

				return Response.ok(newResource).build();
			}
			else {
				throw new NotAuthorizedException("Invalid role");
			}
		}
		catch (Throwable t) {
			return generateErrorResponse(t);
		}
	}

	@PUT
	@Path("{id}")
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public Response updateResource(User resource) {
		try {
			verifyToken();

			User	found = service.getResource(id);
			if ((found == null) || (updateAllowed(found.getChurchId()) == false)) {
				throw new NotFoundException("Invalid role");
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

			return Response.ok(
					service.updateResource(resource)
			).build();
		}
		catch (Throwable t) {
			return generateErrorResponse(t);
		}
	}

	@DELETE
	@Path("{id}")
	@Produces({MediaType.APPLICATION_JSON})
    public Response deleteResource() {
		
		try {
			verifyToken();
			return Response.ok(service.deleteResource(id)).build();
		}
		catch (Throwable t) {
			return generateErrorResponse(t);
		}
    }

	@POST
	@Path("auth/jwt")
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public Response authResource(User resource) {

		try {
			return Response.ok(service.authenticate(resource, getRemoteAddr())).build();
		}
		catch (Throwable t) {
			return generateErrorResponse(t);
		}
	}

}
