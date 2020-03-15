/*
 */
package com.churchclerk.userapi.service;


import com.churchclerk.userapi.model.User;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 *
 */
@SpringBootTest
@TestPropertySource(locations="classpath:application-mock.properties")
public class UserServiceTest {

	@InjectMocks
	private UserService		testObject;

	@Mock
	private UserStorage 	storage;;

	@Value("${jwt.secret}")
	private String			testSecret;

	private String			testId;
	private User			testData;
	private UserEntity 		testEntity;

	private UserResourceSpec resourceSpec = null;


	@BeforeEach
	public void setupMock() {
		testId		= UUID.randomUUID().toString();
		testData 	= new User();
		testEntity	= new UserEntity();
	}

	@Test
	@Order(0)
	public void contexLoads() throws Exception {
		Assertions.assertThat(testObject).isNotNull();
	}

	@Test
	public void testGetResources() throws Exception {
		Pageable pageable = PageRequest.of(0, 10, createSort());

		resourceSpec = new UserResourceSpec(testData);

		Mockito.when(storage.findAll(resourceSpec, pageable)).thenReturn(null);
		Page<? extends User> actual = testObject.getResources(pageable, testData);

		Assertions.assertThat(actual).isNull();
	}

	private Sort createSort() {
		return createSort(null);
	}

	private Sort createSort(String sortBy) {
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

	@Test
	public void testGetResource() throws Exception {
		String	id = "TEST_ID";

		Mockito.when(storage.findById(id)).thenReturn(Optional.of(testEntity));
		User actual = testObject.getResource(id);

		Assertions.assertThat(actual).isEqualTo(testEntity);
	}

	@Test
	public void testCreateResource() throws Exception {

		Mockito.when(storage.save(Mockito.any(UserEntity.class))).thenReturn(testEntity);

		User actual = testObject.createResource(testData);

		Assertions.assertThat(actual).isEqualTo(testEntity);
	}

	@Test
	public void testUpdateResource() throws Exception {
		testData.setName(testId);

		Mockito.when(storage.findById(testData.getName())).thenReturn(Optional.of(testEntity));
		Mockito.when(storage.save(testEntity)).thenReturn(testEntity);

		User actual = testObject.updateResource(testData);

		Assertions.assertThat(actual).isEqualTo(testEntity);
	}

	@Test
	public void testUpdateResourceNotExist() throws Exception {
		testData.setName(testId);

		Mockito.when(storage.findById(testData.getName())).thenReturn(Optional.ofNullable(null));

		User actual = testObject.updateResource(testData);

		Assertions.assertThat(actual).isEqualTo(testData);
	}

	@Test
	public void testDeleteResource() throws Exception {
		testData.setName(testId);

		Mockito.when(storage.findById(testData.getName())).thenReturn(Optional.of(testEntity));
		//Mockito.when(storage.deleteById(testData.getName()));

		User actual = testObject.deleteResource(testData.getName());

		Assertions.assertThat(actual).isEqualTo(testEntity);
	}

	@Test
	public void testAuthenticate() throws Exception {
		ReflectionTestUtils.setField(testObject, "secret", testSecret);

		testData.setName(testId);
		testData.setToken("test");

		testEntity.setToken(encryptToken(testData.getToken()));

		Mockito.when(storage.findById(testData.getName())).thenReturn(Optional.of(testEntity));

		String actual = testObject.authenticate(testData, "127.0.0.1");

		Assertions.assertThat(actual).isNotNull();
	}

	private String encryptToken(String token) {
		BCryptPasswordEncoder	pe	= new BCryptPasswordEncoder();

		return pe.encode(token == null ? "" : token);
	}

}
