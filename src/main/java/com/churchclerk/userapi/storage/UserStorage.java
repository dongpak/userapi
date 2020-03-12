/**
 * 
 */
package com.churchclerk.userapi.storage;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;


/**
 * 
 * @author dongp
 *
 */
public interface UserStorage extends CrudRepository<UserEntity, String>, JpaSpecificationExecutor<UserEntity> {

}
