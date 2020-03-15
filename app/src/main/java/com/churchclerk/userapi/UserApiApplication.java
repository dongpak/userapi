/**
 * 
 */
package com.churchclerk.userapi;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 
 * @author dongp
 *
 */
@ComponentScan({"com.churchclerk"})
@SpringBootApplication

public class UserApiApplication {

	private static Logger logger = LoggerFactory.getLogger(UserApiApplication.class);

	/**
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		SpringApplication.run(UserApiApplication.class, args);
	}

}
