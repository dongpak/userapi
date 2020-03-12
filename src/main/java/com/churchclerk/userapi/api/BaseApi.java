/**
 * 
 */
package com.churchclerk.userapi.api;

import com.churchclerk.securityapi.SecurityApi;
import com.churchclerk.securityapi.SecurityToken;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.util.UUID;


/**
 * 
 * @author dongp
 *
 */
public abstract class BaseApi {

	public enum Role {
		SUPER, ADMIN, CLERK, OFFICIAL, MEMBER, NONMEMbER
	}

	private Logger logger;

	@Context
	protected HttpServletRequest httpRequest;

	@Value("${jwt.secret}")
	private String	secret;

	protected SecurityToken	authToken			= null;
	protected String		requesterId			= null;
	protected UUID			requesterChurchId	= null;

	private Role[]		readRoles			= new Role[0];
	private Role[]		createRoles			= new Role[0];
	private Role[]		updateRoles			= new Role[0];
	private Role[]		deleteRoles			= new Role[0];

	/**
	 * 
	 * @param logger
	 */
	public BaseApi(Logger logger) {
		this.logger = logger;		
	}

	/**
	 *
	 * @param roles
	 */
	protected void setReadRoles(Role... roles) {
		this.readRoles = roles;
	}

	/**
	 *
	 * @param roles
	 */
	protected void setCreateRoles(Role... roles) {
		this.createRoles = roles;
	}

	/**
	 *
	 * @param roles
	 */
	protected void setUpdateRoles(Role... roles) {
		this.updateRoles = roles;
	}

	/**
	 *
	 * @param roles
	 */
	protected void setDeleteRoles(Role... roles) {
		this.deleteRoles = roles;
	}

	/**
	 *
	 * @return
	 * @throws Exception
	 */
	public SecurityToken verifyToken() throws Exception {

		SecurityToken	token	= new SecurityToken();
		String 			auth 	= httpRequest.getHeader("Authorization");

		if (auth == null) {
			logger.info("Authorization header required");
			throw new NotAuthorizedException("Authorization reuired");
		}

		token.setSecret(secret);
		token.setJwt(auth.substring(7));

		if (SecurityApi.process(token) == true) {
			if (token.expired()) {
				logger.info("Token expired");
				throw new NotAuthorizedException("Token expired");
			}

			if (token.getLocation().equals(getRemoteAddr()) == false) {
				logger.info("Invalid location: " + getRemoteAddr());

				throw new NotAuthorizedException("Invalid location");
			}

			authToken 	= token;

			parseRequesterId();
			return token;
		}

		throw new NotAuthorizedException("Bad token");
	}

	/**
	 *
	 * @return
	 */
	public String getRemoteAddr() {

		String addr = httpRequest.getHeader("x-forwarded-for");
		if ((addr != null) && (addr.trim().isEmpty() == false)) {
			return addr;
		}

		return httpRequest.getRemoteAddr();
	}

	private void parseRequesterId() {
		String[]	items = authToken.getId().split("\\|");

		requesterId			= items[0];

		if (items.length > 1) {
			requesterChurchId = UUID.fromString(items[1]);
		}
	}

	/**
	 *
	 * @return
	 */
	protected boolean hasSuperRole() {
		return authToken.getRoles().contains(Role.SUPER.name());
	}

	/**
	 *
	 * @return
	 */
	protected boolean hasAdminRole() {
		return authToken.getRoles().contains(Role.ADMIN.name());
	}

	/**
	 *
	 * @return
	 */
	protected boolean hasClerkRole() {
		return authToken.getRoles().contains(Role.CLERK.name());
	}

	/**
	 *
	 * @return
	 */
	protected boolean hasOfficialRole() {
		return authToken.getRoles().contains(Role.CLERK.name()) || authToken.getRoles().contains(Role.OFFICIAL.name());
	}

	/**
	 *
	 * @return
	 */
	protected boolean hasMemberRole() {
		return authToken.getRoles().contains(Role.MEMBER.name());
	}

	/**
	 *
	 * @param churchId
	 * @return
	 */
	private boolean operationAllowed(UUID churchId, Role[] roles) {
		if (hasSuperRole()) {
			return true;
		}

		if ((requesterChurchId != null) && (requesterChurchId.equals(churchId))) {
			for (Role role : roles) {
				if (authToken.getRoles().contains(role.name())) {
					return true;
				}
			}
		}

		return false;
	}

	/**
	 *
	 * @param churchId
	 * @return
	 */
	protected boolean readAllowed(UUID churchId) {
		return operationAllowed(churchId, readRoles);
	}

	/**
	 *
	 * @param churchId
	 * @return
	 */
	protected boolean createAllowed(UUID churchId) {
		return operationAllowed(churchId, createRoles);
	}

	/**
	 *
	 * @param churchId
	 * @return
	 */
	protected boolean updateAllowed(UUID churchId) {
		return operationAllowed(churchId, updateRoles);
	}

	/**
	 *
	 * @param churchId
	 * @return
	 */
	protected boolean deleteAllowed(UUID churchId) {
		return operationAllowed(churchId, deleteRoles);
	}


	/**
	 * 
	 * @param t
	 * @return
	 */
	protected Response generateErrorResponse(Throwable t) {
		Response r = null;
		
		if (t instanceof NotAuthorizedException) {
			r = Response.status(Status.UNAUTHORIZED).build();
		}
		else if (t instanceof NotFoundException) {
			r = Response.status(Status.NOT_FOUND).build();
		}
		else {
			r = Response.serverError().build();
			logger.error("Generating " + r.getStatus() + " " + r.getStatusInfo().getReasonPhrase() + " for "+ t, t);
		}

		return r;
	}


}
