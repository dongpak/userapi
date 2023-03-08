/**
 * 
 */
package com.churchclerk.userapi.service;

import com.churchclerk.userapi.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;


/**
 * 
 * @author dongp
 *
 */
public interface UserStorage extends JpaRepository<UserEntity, UUID>, CrudRepository<UserEntity, UUID>, JpaSpecificationExecutor<UserEntity> {

}
