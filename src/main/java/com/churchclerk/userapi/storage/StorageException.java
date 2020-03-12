/**
 * 
 */
package com.churchclerk.userapi.storage;

/**
 * @author dongp
 *
 */
public class StorageException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * 
	 */
	public StorageException() {
	}

	/**
	 * @param message
	 */
	public StorageException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public StorageException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public StorageException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public StorageException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
