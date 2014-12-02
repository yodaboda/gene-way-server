package com.nutrinfomics.geneway.server;

import java.io.IOException;
import java.io.Reader;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

public class DatabaseConnection {

	private static DatabaseConnection instance = new DatabaseConnection();
	
	private SqlSessionFactory sqlSessionFactory = null;
	
	private DatabaseConnection(){
		String resource;
		if(isSuperDevMode()){
			resource = "com/nutrinfomics/geneway/server/data/configuration.xml";
		}
		else{
			resource = "src/com/nutrinfomics/geneway/server/data/configuration.xml";
		}
		Reader reader = null;
		try {
			reader = Resources.getResourceAsReader(resource);
			
			sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private boolean isSuperDevMode() {
		return true;
//		return GWT.getModuleBaseURL() != GWT.getModuleBaseForStaticFiles();
	}

	public static DatabaseConnection getInstance(){
		return instance;
	}
	
	public SqlSession getSession(){
		return sqlSessionFactory.openSession();
	}
}

