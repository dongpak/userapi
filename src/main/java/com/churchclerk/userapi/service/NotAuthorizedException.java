/**
 * 
 */
package com.churchclerk.userapi.service;

/**
 * @author dongp
 *
 */
public class NotAuthorizedException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * 
	 */
	public NotAuthorizedException() {
	}

	/**
	 * @param message
	 */
	public NotAuthorizedException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public NotAuthorizedException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public NotAuthorizedException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public NotAuthorizedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
