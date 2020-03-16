/*
 */
package com.churchclerk.userapi.api;


import com.churchclerk.baseapi.model.ApiCaller;
import com.churchclerk.securityapi.SecurityApi;
import com.churchclerk.securityapi.SecurityToken;
import com.churchclerk.userapi.entity.UserEntity;
import com.churchclerk.userapi.model.User;
import com.churchclerk.userapi.service.UserService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.util.ReflectionTestUtils;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import java.util.Date;
import java.util.UUID;

/**
 *
 */
@SpringBootTest
@TestPropertySource(locations="classpath:application-mock.properties")
public class UserAuthTest {

	private static final String HEADER_AUTHORIZATION	= "Authorization";
	private static final String TOKEN_PREFIX 			= "Bearer ";
	private static final String LOCAL_ADDRESS			= "127.0.0.1";

	@InjectMocks
	private UserAuth				testObject;

	@Mock
	protected HttpServletRequest	testHttpRequest;

	@Mock
	private UserService 			testService;

	@Value("${jwt.secret}")
	private String					testSecret;


	private SecurityToken	testToken;
	private Date			testDate;
	private String			testId;
	private User			testResource;
	private UserEntity 		testEntity;

	@BeforeEach
	public void setupMock() {

		Mockito.clearInvocations(testHttpRequest);
		Mockito.clearInvocations(testService);

		testDate		= new Date();
		testId			= UUID.randomUUID().toString();
		testResource	= createUser(testId);
		testEntity		= new UserEntity();

		if (createToken(testId, LOCAL_ADDRESS) == false) {
			throw new RuntimeException("Error creating security token");
		};

		ReflectionTestUtils.setField(testObject, "secret", testSecret);
	}

	private User createUser(String name) {
		User	resource = new User();

		resource.setName(name);
		resource.setRoles(ApiCaller.Role.ADMIN.name());
		resource.setToken(UUID.randomUUID().toString());
		resource.setActive(true);
		return resource;
	}

	private boolean createToken(String id, String location) {
		testToken = new SecurityToken();

		testToken.setId(id + "|");
		testToken.setRoles(ApiCaller.Role.SUPER.name());
		testToken.setLocation(location);
		testToken.setSecret(testSecret);

		return SecurityApi.process(testToken);
	}

	@Test
	@Order(0)
	public void contexLoads() throws Exception {
		Assertions.assertThat(testObject).isNotNull();
	}


	@Test
	public void testAuthResource() throws Exception {
		ReflectionTestUtils.setField(testObject, "id", testId);

		Mockito.when(testHttpRequest.getHeader(HEADER_AUTHORIZATION)).thenReturn(TOKEN_PREFIX+testToken.getJwt());
		Mockito.when(testHttpRequest.getRemoteAddr()).thenReturn(LOCAL_ADDRESS);
		Mockito.when(testService.authenticate(testResource, LOCAL_ADDRESS)).thenReturn("JWT Token");

		Response response = testObject.authResource(testResource);

		Assertions.assertThat(response.getEntity()).isNotNull();
		Assertions.assertThat(response.getEntity()).isEqualTo("JWT Token");
	}
}
