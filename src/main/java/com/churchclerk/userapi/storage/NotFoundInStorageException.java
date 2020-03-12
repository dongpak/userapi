/**
 * 
 */
package com.churchclerk.userapi.storage;

/**
 * @author dongp
 *
 */
public class NotFoundInStorageException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * 
	 */
	public NotFoundInStorageException() {
	}

	/**
	 * @param message
	 */
	public NotFoundInStorageException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public NotFoundInStorageException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public NotFoundInStorageException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public NotFoundInStorageException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
