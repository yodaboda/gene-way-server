package com.nutrinfomics.geneway.server.requestfactory.request;

import java.io.Serializable;

public class RegisterException extends Exception implements Serializable {
	public enum RegisterExceptionType{USERNAME_EXISTS, EMAIL_EXISTS}
	
	private RegisterExceptionType type;
	
	public RegisterException(){
		
	}
	
	public RegisterException(RegisterExceptionType type){
		this.setType(type);
	}

	public RegisterExceptionType getType() {
		return type;
	}

	public void setType(RegisterExceptionType type) {
		this.type = type;
	}
	
	@Override
	public String getMessage(){
		return type.toString();
	}
}
