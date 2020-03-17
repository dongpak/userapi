/*
 */
package com.churchclerk.userapi.api;


import com.churchclerk.baseapi.model.ApiCaller;
import com.churchclerk.securityapi.SecurityApi;
import com.churchclerk.securityapi.SecurityToken;
import com.churchclerk.userapi.model.User;
import com.churchclerk.userapi.service.UserService;
import com.churchclerk.userapi.entity.UserEntity;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.util.ReflectionTestUtils;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import java.security.Principal;
import java.util.Date;
import java.util.UUID;

/**
 *
 */
@SpringBootTest
@TestPropertySource(locations="classpath:application-mock.properties")
public class UserApiTest {

	private static final String HEADER_AUTHORIZATION	= "Authorization";
	private static final String TOKEN_PREFIX 			= "Bearer ";
	private static final String LOCAL_ADDRESS			= "127.0.0.1";

	@InjectMocks
	private UserApi					testObject;

	@Mock
	protected HttpServletRequest	testHttpRequest;

	@Mock
	private UserService 			testService;

	@Value("${jwt.secret}")
	private String					testSecret;

	private String			testCallerId;
	private SecurityToken	testToken;
	private Principal 		testPrincipal;
	private Date			testDate;
	private String			testChurchId;
	private User			testResource;
	private UserEntity 		testEntity;

	@BeforeEach
	public void setupMock() {

		Mockito.clearInvocations(testHttpRequest);
		Mockito.clearInvocations(testService);

		testCallerId	= "testCaller";
		testDate		= new Date();
		testChurchId	= UUID.randomUUID().toString();
		testResource	= createResource(0);
		testEntity		= new UserEntity();

		if (createToken(testCallerId, LOCAL_ADDRESS) == false) {
			throw new RuntimeException("Error creating security token");
		};

		testPrincipal   = new UsernamePasswordAuthenticationToken(null, testToken, null);

		Mockito.when(testHttpRequest.getHeader(HEADER_AUTHORIZATION)).thenReturn(TOKEN_PREFIX+testToken.getJwt());
		Mockito.when(testHttpRequest.getRemoteAddr()).thenReturn(LOCAL_ADDRESS);
		Mockito.when(testHttpRequest.getUserPrincipal()).thenReturn(testPrincipal);

		ReflectionTestUtils.setField(testObject, "secret", testSecret);
	}

	private User createResource(int number) {
		User	resource = new User();

		resource.setName("TestUser" + number);
		resource.setRoles(ApiCaller.Role.ADMIN.name());
		resource.setToken(UUID.randomUUID().toString());
		resource.setChurchId(testChurchId);
		resource.setActive(true);
		return resource;
	}

	private boolean createToken(String id, String location) {
		testToken = new SecurityToken();

		testToken.setId(testCallerId + "|");
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
	public void testGetResources() throws Exception {
		ReflectionTestUtils.setField(testObject, "sortBy", "street");

		Mockito.when(testService.getResources(null, null)).thenReturn(null);

		Response response = testObject.getResources();

		Assertions.assertThat(response.getEntity()).isNull();
	}


	@Test
	public void testGetResource() throws Exception {

		ReflectionTestUtils.setField(testObject, "id", testResource.getName());

		Mockito.when(testService.getResource(testResource.getName())).thenReturn(testResource);

		Response response = testObject.getResource();

		Assertions.assertThat(response.getEntity()).isNotNull();
		Assertions.assertThat(response.getEntity()).isEqualTo(testResource);
	}

	@Test
	public void testCreateResource() throws Exception {

		Mockito.when(testService.createResource(testResource)).thenReturn(testResource);

		Response response = testObject.createResource(testResource);

		Assertions.assertThat(response).isNotNull();
		Assertions.assertThat(response.getStatus()).isBetween(200, 299);
		Assertions.assertThat(response.getEntity()).isNotNull();
		Assertions.assertThat(response.getEntity()).isInstanceOf(User.class);

		User actual = (User) response.getEntity();
		Assertions.assertThat(actual.getName()).isNotNull();
		Assertions.assertThat(actual.getName()).isEqualTo(actual.getName());
		Assertions.assertThat(actual.isActive()).isEqualTo(true);
		Assertions.assertThat(actual.getCreatedBy()).isEqualTo(testCallerId);
		Assertions.assertThat(actual.getCreatedDate()).isAfterOrEqualTo(testDate);
		Assertions.assertThat(actual.getUpdatedBy()).isEqualTo(testCallerId);
		Assertions.assertThat(actual.getUpdatedDate()).isAfterOrEqualTo(testDate);
	}

	@Test
	public void testUpdateResource() throws Exception {

		ReflectionTestUtils.setField(testObject, "id", testResource.getName());

		Mockito.when(testService.getResource(testResource.getName())).thenReturn(testResource);
		Mockito.when(testService.updateResource(testResource)).thenReturn(testResource);

		testResource.setActive(false);

		Response response = testObject.updateResource(testResource);

		Assertions.assertThat(response.getEntity()).isNotNull();
		Assertions.assertThat(response.getEntity()).isInstanceOf(User.class);

		User actual = (User) response.getEntity();
		Assertions.assertThat(actual.getName()).isNotNull();
		Assertions.assertThat(actual.getName()).isEqualTo(testResource.getName());
		Assertions.assertThat(actual.isActive()).isEqualTo(false);
		Assertions.assertThat(actual.getUpdatedBy()).isEqualTo(testCallerId);
		Assertions.assertThat(actual.getUpdatedDate()).isAfterOrEqualTo(testDate);
	}

	@Test
	public void testUpdateResourceNotExist() throws Exception {
		ReflectionTestUtils.setField(testObject, "id", testResource.getName());

		Mockito.when(testService.getResource(testResource.getName())).thenReturn(null);
		Mockito.when(testService.updateResource(testResource)).thenReturn(null);

		Response response = testObject.updateResource(testResource);

		Assertions.assertThat(response.getStatus()).isEqualTo(404);
	}

	@Test
	public void testDeleteResource() throws Exception {
		ReflectionTestUtils.setField(testObject, "id", testResource.getName());

		Mockito.when(testService.getResource(testResource.getName())).thenReturn(testResource);
		Mockito.when(testService.deleteResource(testResource.getName())).thenReturn(testResource);

		Response response = testObject.deleteResource();

		Assertions.assertThat(response.getEntity()).isNotNull();
		Assertions.assertThat(response.getEntity()).isEqualTo(testResource);
	}
}
