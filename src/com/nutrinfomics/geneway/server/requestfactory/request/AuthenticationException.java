package com.nutrinfomics.geneway.server.requestfactory.request;

import java.io.Serializable;

public class AuthenticationException extends Exception implements Serializable {
	public enum LoginExceptionType{INVALID_USERNAME, UNAUTHORIZED_DEVICE, INVALID_PASSWORD, INVALID_SESSION}
	
	private LoginExceptionType type;
	
	public AuthenticationException(){
		
	}
	
	public AuthenticationException(LoginExceptionType type){
		this(type, "");
	}

	public AuthenticationException(LoginExceptionType type, String message){
		super(message);
		this.setType(type);
	}
	
	public LoginExceptionType getType() {
		return type;
	}

	public void setType(LoginExceptionType type) {
		this.type = type;
	}
	
	@Override
	public String getMessage(){
		return type.toString();
	}
}
