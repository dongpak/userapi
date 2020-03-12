/**
 * 
 */
package com.churchclerk.userapi.storage;

/**
 * @author dongp
 *
 */
public class DuplicateInStorageException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * 
	 */
	public DuplicateInStorageException() {
	}

	/**
	 * @param message
	 */
	public DuplicateInStorageException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public DuplicateInStorageException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public DuplicateInStorageException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public DuplicateInStorageException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
