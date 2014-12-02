package com.nutrinfomics.geneway.server;

import org.apache.ibatis.session.SqlSession;

import com.nutrinfomics.geneway.server.data.UserMapper;
import com.nutrinfomics.geneway.server.domain.customer.Customer;
import com.nutrinfomics.geneway.server.domain.device.Session;

abstract public class AbstractAuthenticatedService{

	protected Customer authenticate(Session session){
		Customer customer = getUser(session);		
		return customer;
	}

	private Customer getUser(Session userSession) {
		SqlSession sqlSession = DatabaseConnection.getInstance().getSession();
		try{
			UserMapper mapper = sqlSession.getMapper(UserMapper.class);
			Customer customer = mapper.selectCustomerForSession(userSession.getSid());
			return customer;
		}
		finally{
			sqlSession.close();
		}
	}

}
