package com.aprendendoSpring.course.service.exceptions;

public class ResourceNotFoundExceptions extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ResourceNotFoundExceptions(Object id) {
		super("Resource Not Found. id "+ id);
	}
	
}
