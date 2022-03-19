package com.matiaszalazar.xcalewhatsapp.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Custom {@link RuntimeException} for manage Null Ids.
 * 
 * @author Matias Zalazar
 *
 */
@SuppressWarnings("serial")
public class NullIdException extends RuntimeException {

	private Logger log = LoggerFactory.getLogger(NullIdException.class);
	
	public NullIdException(String message) {
		super(message);
		log.error(message);
	}
}