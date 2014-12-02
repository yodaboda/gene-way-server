package com.nutrinfomics.geneway.server.domain.authentication;

import java.util.UUID;

import com.nutrinfomics.geneway.server.BCrypt;
import com.nutrinfomics.geneway.server.data.UserMapperServices;
import com.nutrinfomics.geneway.server.domain.authentication.AuthenticationException.LoginExceptionType;
import com.nutrinfomics.geneway.server.domain.customer.Customer;
import com.nutrinfomics.geneway.server.domain.device.Device;
import com.nutrinfomics.geneway.server.domain.device.Session;

public class Authentication {
	public static Customer authenticateCustomer(String userName, String password, String uuid) throws AuthenticationException{
		Customer customer = UserMapperServices.getInstance().selectCustomer(userName);
		
		if(customer == null){
			throw new AuthenticationException(LoginExceptionType.INVALID_USERNAME);
		}

		Session session = new Session();
		UUID sessionID = UUID.randomUUID();
		session.setSid(sessionID.toString());
		session.setCustomer(customer);
		
		String hashedPassword = UserMapperServices.getInstance().getCustomerHashedPassword(customer);
		if(hashedPassword == null){ // first-time login
			hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
			customer.setPassword(hashedPassword);
			UserMapperServices.getInstance().updateCustomerHashedPassword(customer);

			Device device = new Device();
			device.setUuid(uuid);
			device.setCustomer(customer);
			
			UserMapperServices.getInstance().insertDevice(device);
			UserMapperServices.getInstance().insertSession(session);
		}
//		String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
		boolean valid = isHashedPasswordEqual(hashedPassword, password);

		if(valid){
			Device deviceDb = UserMapperServices.getInstance().getCustomerDevice(customer);

			if(deviceDb == null || !deviceDb.getUuid().equalsIgnoreCase(uuid)){
				AuthenticationException loginException = new AuthenticationException(LoginExceptionType.UNAUTHORIZED_DEVICE);
				throw loginException;
			}
			
			UserMapperServices.getInstance().updateSession(session);

			customer.setDevice(deviceDb);
			customer.setSession(UserMapperServices.getInstance().getSession(customer));
			return customer;
		}
		else{
			throw new AuthenticationException(LoginExceptionType.INVALID_PASSWORD);
		}
	}
	
	public static Customer authenticateSession(String sid, String uuid) throws AuthenticationException{
		Customer customer = UserMapperServices.getInstance().getCustomer(sid);
		Device device = UserMapperServices.getInstance().getCustomerDevice(customer);
		Session session = UserMapperServices.getInstance().getSession(customer);

		if( ! device.getUuid().equalsIgnoreCase(uuid) ||
				! session.getSid().equalsIgnoreCase(sid) ){
			throw new AuthenticationException(LoginExceptionType.INVALID_SESSION);
		}
		
		customer.setDevice(device);
		device.setCustomer(customer);
		
		customer.setSession(session);
		session.setCustomer(customer);
		
		return customer;

	}

	public static Customer registerCustomer(String username, String password, String uuid){
		return null;
	}
	
	private static boolean isHashedPasswordEqual(String hashedPassword, String plainTextPassword){
		if(hashedPassword == null || plainTextPassword == null) return false;
		return BCrypt.checkpw(plainTextPassword, hashedPassword);
	}

}
