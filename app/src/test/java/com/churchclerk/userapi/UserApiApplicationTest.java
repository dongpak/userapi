/*
 */
package com.churchclerk.userapi;

import com.churchclerk.baseapi.BaseApi;
import com.churchclerk.userapi.api.UserApi;
import com.churchclerk.securityapi.SecurityApi;
import com.churchclerk.securityapi.SecurityToken;
import com.churchclerk.userapi.model.User;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.data.domain.Sort;
import org.springframework.http.*;

import java.net.Inet4Address;
import java.util.Iterator;
import java.util.UUID;

/**
 *
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserApiApplicationTest {

	private static final String 	TOKEN_PREFIX 	= "Bearer ";
	private static final String 	HEADER_AUTH 	= "Authorization";


	@LocalServerPort
	private int 		port;

	@Value("${jwt.secret}")
	private String		testSecret;

	@Autowired
	private UserApi api;

	@Autowired
	private TestRestTemplate	restTemplate;

	private SecurityToken		testToken;

	private HttpHeaders 		testHeaders;

	@BeforeEach
	public void setupMock() {

		try {
			if (createToken("test", Inet4Address.getLoopbackAddress().getHostAddress()) == false) {
				throw new RuntimeException("Error creating security token");
			}

			testHeaders = new HttpHeaders();
			testHeaders.add(HEADER_AUTH, TOKEN_PREFIX+testToken.getJwt());
			testHeaders.add("Content-Type", "application/json");
		}
		catch (Exception e) {
			throw new RuntimeException("Error creating security token", e);
		}
	}

	private boolean createToken(String id, String location) {
		testToken = new SecurityToken();

		testToken.setId(id + "|");
		testToken.setRoles(BaseApi.Role.SUPER.name());
		testToken.setLocation(location);
		testToken.setSecret(testSecret);

		return SecurityApi.process(testToken);
	}

	@Test
	@Order(0)
	public void contexLoads() throws Exception {
		Assertions.assertThat(api).isNotNull();
	}

	@Test
	@Order(1)
	public void testGetResources() throws Exception {

		getResourcesAndCheck(createUrl(), 1L);
	}

	private String createUrl() {
		return createUrl(null);
	}

	private String createUrl(String id) {
		StringBuffer	buffer = new StringBuffer("http://localhost:");

		buffer.append(port);
		buffer.append("/api/user");
		if (id != null) {
			buffer.append("/");
			buffer.append(id);
		}

		return buffer.toString();
	}

	private JsonObject getResourcesAndCheck(String url, long count) {

		HttpEntity<String>		entity 		= new HttpEntity<String>(testHeaders);
		ResponseEntity<String>	response	= restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

		Assertions.assertThat(response).isNotNull();
		Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

		JsonObject page = new Gson().fromJson(response.getBody(), JsonObject.class);

		Assertions.assertThat(page.get("numberOfElements").getAsLong()).isEqualTo(count);
		return page;
	}

	@Test
	@Order(2)
	public void testPostResource() throws Exception {

		User testdata = createUser(1000);

		createResourceAndCheck(testdata);
	}

	private User createUser(int number) {
		User resource = new User();

		resource.setName("TestUser" + number);
		resource.setToken("TestToken");
		resource.setRoles("admin");
		resource.setChurchId(UUID.randomUUID().toString());
		resource.setActive(true);

		return resource;
	}

	/**
	 *
	 * @param expected
	 * @return posted resource
	 */
	private User createResourceAndCheck(User expected) {

		HttpEntity<User>		entity 		= new HttpEntity<User>(expected, testHeaders);
		ResponseEntity<User>	response	= restTemplate.exchange(createUrl(), HttpMethod.POST, entity, User.class);

		Assertions.assertThat(response).isNotNull();
		Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

		User	actual = response.getBody();

		Assertions.assertThat(actual).isNotNull();

		Assertions.assertThat(actual.getName()).isNotNull();
		Assertions.assertThat(actual.isActive()).isEqualTo(expected.isActive());
		Assertions.assertThat(actual.getCreatedDate()).isNotNull();
		Assertions.assertThat(actual.getCreatedBy()).isNotNull();
		Assertions.assertThat(actual.getUpdatedDate()).isNotNull();
		Assertions.assertThat(actual.getUpdatedBy()).isNotNull();

		//Assertions.assertThat(actual.getToken()).isEqualTo(expected.getToken());
		Assertions.assertThat(actual.getRoles()).isEqualTo(expected.getRoles());
		Assertions.assertThat(actual.getChurchId()).isEqualTo(expected.getChurchId());

		return actual;
	}

	@Test
	@Order(3)
	public void testGetResource() throws Exception {

		User	testdata 	= createUser(1001);
		User	expected	= createResourceAndCheck(testdata);

		HttpEntity<User>		entity 		= new HttpEntity<User>(testHeaders);
		ResponseEntity<User>	response	= restTemplate.exchange(createUrl(expected.getName()), HttpMethod.GET, entity, User.class);

		Assertions.assertThat(response).isNotNull();
		Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

		User	actual = response.getBody();

		Assertions.assertThat(actual).isNotNull();
		Assertions.assertThat(actual).isEqualTo(expected);
	}

	@Test
	@Order(4)
	public void testUpdateResource() throws Exception {

		User	testdata 	= createUser(1002);
		User	expected	= createResourceAndCheck(testdata);

		expected.setActive(false);
		expected.setToken(null);

		HttpEntity<User>		entity 		= new HttpEntity<User>(expected, testHeaders);
		ResponseEntity<User>	response	= restTemplate.exchange(createUrl(expected.getName()), HttpMethod.PUT, entity, User.class);

		Assertions.assertThat(response).isNotNull();
		Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

		User	actual = response.getBody();

		Assertions.assertThat(actual).isNotNull();
		Assertions.assertThat(actual.getUpdatedDate()).isAfterOrEqualTo(expected.getUpdatedDate());

		expected.setToken(actual.getToken());
		expected.setUpdatedDate(actual.getUpdatedDate());
		Assertions.assertThat(actual).isEqualTo(expected);
	}

	@Test
	@Order(5)
	public void testDeleteResource() throws Exception {

		User	testdata 	= createUser(1003);
		User	expected	= createResourceAndCheck(testdata);

		// delete
		HttpEntity<User>		entity 		= new HttpEntity<User>(testHeaders);
		ResponseEntity<User>	response	= restTemplate.exchange(createUrl(expected.getName()), HttpMethod.DELETE, entity, User.class);

		Assertions.assertThat(response).isNotNull();
		Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

		// try getting the deleted resource
		HttpEntity<User>		entity2 	= new HttpEntity<User>(testHeaders);
		ResponseEntity<User>	response2	= restTemplate.exchange(createUrl(expected.getName()), HttpMethod.DELETE, entity2, User.class);

		Assertions.assertThat(response2).isNotNull();
		Assertions.assertThat(response2.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

	@Test
	@Order(6)
	public void testGetResourcesPagination() throws Exception {

		createResourceAndCheck(createUser(1004));
		createResourceAndCheck(createUser(1005));

		getResourcesAndCheck(createPaginationUrl(0, 1), 1L);
		getResourcesAndCheck(createPaginationUrl(0, 2), 2L);
		getResourcesAndCheck(createPaginationUrl(1, 1), 1L);
		getResourcesAndCheck(createPaginationUrl(9, 5), 0L);
	}

	private String createPaginationUrl(int page, int size) {
		StringBuffer buffer = new StringBuffer(createUrl());

		buffer.append("?page=").append(page);
		buffer.append("&size=").append(size);

		return buffer.toString();
	}

	@Test
	@Order(7)
	public void testGetResourcesFilter() throws Exception {

		createResourceAndCheck(createUser(1006));
		createResourceAndCheck(createUser(1007));

		getResourcesAndCheck(createFilterUrl("name", "%1006"), 1L);
	}

	private String createFilterUrl(String field, String value) {
		StringBuffer buffer = new StringBuffer(createUrl());

		buffer.append("?");
		buffer.append(field);
		buffer.append("=");
		buffer.append(value);

		return buffer.toString();
	}

	@Test
	@Order(8)
	public void testGetResourcesSort() throws Exception {

		createResourceAndCheck(createUser(1008));
		createResourceAndCheck(createUser(1009));

		getResourcesAndCheck(createSortUrl("name"), 9L, Sort.Direction.ASC);
		getResourcesAndCheck(createSortUrl("-name"), 9L, Sort.Direction.DESC);
	}

	private String createSortUrl(String keys) {
		StringBuffer buffer = new StringBuffer(createUrl());

		buffer.append("?sortBy=");
		buffer.append(keys);

		return buffer.toString();
	}

	private void getResourcesAndCheck(String url, long count, final Sort.Direction dir) {
		JsonObject 	page 		= getResourcesAndCheck(url, count);
		JsonArray	content		= page.getAsJsonArray("content");
		String 		previous 	= null;

		Iterator<JsonElement> iter = content.iterator();

		while (iter.hasNext()) {
			String name = iter.next().getAsJsonObject().get("name").getAsString();

			if (previous != null) {
				if (dir.equals(Sort.Direction.ASC)) {
					Assertions.assertThat(name).isGreaterThanOrEqualTo(previous);
				}
				else {
					Assertions.assertThat(name).isLessThanOrEqualTo(previous);
				}
			}
			previous = name;
		}
	}

	@Test
	@Order(9)
	public void testAuthResource() throws Exception {

		createResourceAndCheck(createUser(1009));

		User					resource	= createUser(1009);
		HttpEntity<User>		entity 		= new HttpEntity<User>(resource, testHeaders);
		ResponseEntity<String>	response	= restTemplate.exchange(createAuthJwtUrl(), HttpMethod.POST, entity, String.class);

		Assertions.assertThat(response).isNotNull();
		Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

		String	actual = response.getBody();

		Assertions.assertThat(actual).isNotNull();
	}

	private String createAuthJwtUrl() {
		StringBuffer	buffer = new StringBuffer("http://localhost:");

		buffer.append(port);
		buffer.append("/api/auth/jwt");

		return buffer.toString();
	}
}
