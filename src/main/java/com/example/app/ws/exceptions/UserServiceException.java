package com.example.app.ws.exceptions;

public class UserServiceException extends RuntimeException{

	private static final long serialVersionUID = -4808502995744716206L;

	public UserServiceException(String message) {
		super(message);
	}
}
