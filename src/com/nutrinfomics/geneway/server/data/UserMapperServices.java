package com.nutrinfomics.geneway.server.data;

import org.apache.ibatis.session.SqlSession;

import com.nutrinfomics.geneway.server.DatabaseConnection;
import com.nutrinfomics.geneway.server.domain.customer.Customer;
import com.nutrinfomics.geneway.server.domain.customer.PersonalDetails;
import com.nutrinfomics.geneway.server.domain.device.Device;
import com.nutrinfomics.geneway.server.domain.device.Session;

public class UserMapperServices {
	private static UserMapperServices instance;
	
	public static UserMapperServices getInstance(){
		if(instance == null){
			synchronized (UserMapperServices.class) {
				if(instance == null){
					instance = new UserMapperServices();
				}
			}
		}
		return instance;
	}
	
	private UserMapperServices(){
		
	}
	
	public Customer selectCustomer(String username) {
		SqlSession session = DatabaseConnection.getInstance().getSession();
		try{
			UserMapper mapper = session.getMapper(UserMapper.class);
			Customer user = mapper.selectCustomer(username);
			return user;
		}
		finally{
			session.close();
		}
	}

	public Customer findCustomer(long id){
		SqlSession session = DatabaseConnection.getInstance().getSession();
		try{
			UserMapper mapper = session.getMapper(UserMapper.class);
			Customer user = mapper.findCustomer(id);
			return user;
		}
		finally{
			session.close();
		}
	}
	
	public String getCustomerHashedPassword(Customer customer) {
		SqlSession session = DatabaseConnection.getInstance().getSession();
		try{
			UserMapper mapper = session.getMapper(UserMapper.class);
			String hashedPassword = mapper.selectCustomerHashedPassword(customer);
			return hashedPassword;
		}
		finally{
			session.close();
		}
	}

	public Device getCustomerDevice(Customer customer) {
		SqlSession session = DatabaseConnection.getInstance().getSession();
		try{
			UserMapper mapper = session.getMapper(UserMapper.class);
			Device device = mapper.selectCustomerDevice(customer);
			return device;
		}
		finally{
			session.close();
		}
	}

	public void updateSession(Session session) {
		SqlSession sqlSession = DatabaseConnection.getInstance().getSession();
		try{
			UserMapper mapper = sqlSession.getMapper(UserMapper.class);
			mapper.updateSession(session);
		}
		finally{
			sqlSession.commit();
			sqlSession.close();
		}
	}

	public Customer getCustomer(String sid) {
		SqlSession sqlSession = DatabaseConnection.getInstance().getSession();
		try{
			UserMapper mapper = sqlSession.getMapper(UserMapper.class);
			Customer customer = mapper.selectCustomerForSession(sid);
			return customer;
		}
		finally{
			sqlSession.close();
		}
	}

	public Session getSession(Customer customer) {
		SqlSession sqlSession = DatabaseConnection.getInstance().getSession();
		try{
			UserMapper mapper = sqlSession.getMapper(UserMapper.class);
			Session session = mapper.selectCustomerSession(customer);
			return session;
		}
		finally{
			sqlSession.close();
		}
	}
	
	public Session findSession(long id) {
		SqlSession sqlSession = DatabaseConnection.getInstance().getSession();
		try{
			UserMapper mapper = sqlSession.getMapper(UserMapper.class);
			Session session = mapper.findSession(id);
			return session;
		}
		finally{
			sqlSession.close();
		}
	}


	
	public void insertCustomer(Customer customer) {
		SqlSession sqlSession = DatabaseConnection.getInstance().getSession();
		try{
			UserMapper mapper = sqlSession.getMapper(UserMapper.class);
			mapper.insertUser(customer);
		}
		finally{
			sqlSession.commit();
			sqlSession.close();
		}
	}

	public void insertPersonalDetails(PersonalDetails personalDetails) {
		SqlSession sqlSession = DatabaseConnection.getInstance().getSession();
		try{
			UserMapper mapper = sqlSession.getMapper(UserMapper.class);
			mapper.insertPersonalDetails(personalDetails);
		}
		finally{
			sqlSession.commit();
			sqlSession.close();
		}
	}

	public void insertSession(Session session) {
		SqlSession sqlSession = DatabaseConnection.getInstance().getSession();
		try{
			UserMapper mapper = sqlSession.getMapper(UserMapper.class);
			mapper.insertSession(session);
		}
		finally{
			sqlSession.commit();
			sqlSession.close();
		}
	}

	public void insertCustomerAssociations(Customer customer) {
		SqlSession sqlSession = DatabaseConnection.getInstance().getSession();
		try{
			UserMapper mapper = sqlSession.getMapper(UserMapper.class);
			mapper.insertCustomerAssociations(customer);
		}
		finally{
			sqlSession.commit();
			sqlSession.close();
		}
	}

	public void updateCustomerHashedPassword(Customer customer) {
		SqlSession sqlSession = DatabaseConnection.getInstance().getSession();
		try{
			UserMapper mapper = sqlSession.getMapper(UserMapper.class);
			mapper.updateCustomerHashedPassword(customer);
		}
		finally{
			sqlSession.commit();
			sqlSession.close();
		}
	}

	public void insertDevice(Device device) {
		SqlSession sqlSession = DatabaseConnection.getInstance().getSession();
		try{
			UserMapper mapper = sqlSession.getMapper(UserMapper.class);
			mapper.insertDevice(device);
		}
		finally{
			sqlSession.commit();
			sqlSession.close();
		}
	}


}
