package com.matiaszalazar.xcalewhatsapp.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Custom {@link RuntimeException} for manage Not Found Resources.
 * 
 * @author Matias Zalazar
 *
 */
@SuppressWarnings("serial")
public class ResourceNotFoundException extends RuntimeException {

	private Logger log = LoggerFactory.getLogger(ResourceNotFoundException.class);
	
	public ResourceNotFoundException(String message) {
		super(message);
		log.error(message);
	}
}
