package com.commercetools.pim.exception;

public class ImporterException extends Exception {

	private static final long serialVersionUID = -5943011489290819532L;
	private final String message;
	
	public ImporterException(String message) {
		super(message);
		this.message = message;
	}
	
	@Override
	public String getMessage() {
		return this.message;
		
	}
}
