package com.wu.achievers.BugTracking.exceptionHandling;

public class NotFoundException extends RuntimeException{
    public NotFoundException() {
		
	}

	public NotFoundException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		
	}

	public NotFoundException(String message, Throwable cause) {
		super(message, cause);
		
	}

	public NotFoundException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public NotFoundException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}
}
