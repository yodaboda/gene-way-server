package com.nutrinfomics.geneway.server.requestfactory;

import java.io.IOException;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.web.bindery.requestfactory.server.DefaultExceptionHandler;
import com.google.web.bindery.requestfactory.server.ExceptionHandler;
import com.google.web.bindery.requestfactory.server.RequestFactoryServlet;
import com.google.web.bindery.requestfactory.server.ServiceLayerDecorator;
import com.nutrinfomics.geneway.server.data.HibernateUtil;
import com.nutrinfomics.geneway.server.domain.customer.Customer;
import com.nutrinfomics.geneway.server.domain.device.Device;
import com.nutrinfomics.geneway.server.domain.device.Session;
import com.nutrinfomics.geneway.shared.AccessConstants;

@Singleton
public class GeneWayRequestFactoryServlet extends RequestFactoryServlet {

//	static private boolean onlyLoginAllowed = false;
	
//	@Inject Provider<EntityManager> em;
	
	@Inject
	public GeneWayRequestFactoryServlet(final ExceptionHandler exceptionHandler, final ServiceLayerDecorator guiceSL){
		super(exceptionHandler, guiceSL, new SecurityLocalizationDecorator());
	}

//	public GeneWayRequestFactoryServlet(){
//		super(new DefaultExceptionHandler(), new SecurityDecorator());
//	}

	
//	@Override
//    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException{
//        req.getLocale();
//		if (! userIsLoggedIn(req)){
//        	res.sendError(HttpServletResponse.SC_UNAUTHORIZED);
////            throw new ServletException("not logged in");
//        }
//        else{
//            super.doPost(req, res);
//        }
//    }
//
//    @Override
//    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException{
//        if (! userIsLoggedIn(req)){
//        	res.sendError(HttpServletResponse.SC_UNAUTHORIZED);
////        	throw new ServletException("not logged in");
//        }
//        else{
//            super.doGet(req, res);
//        }
//    }
//    protected boolean userIsLoggedIn(HttpServletRequest req){
//    	String sid = (String) req.getHeader(AccessConstants.SID.toString());
//    	String uuid = (String) req.getHeader(AccessConstants.UUID.toString());
//        
//    	
//    	if(sid == null){
//    		setOnlyLoginAllowed(true);
//    		return true; // user did not log-in yet. SecurityDecorator will allow only logging-in
//    	}
//    	
//    	setOnlyLoginAllowed(false);
//    	
//		Session sessionDb = new HibernateUtil().selectSession(sid, em);
//
//		Customer customerDb = sessionDb.getCustomer();
//		Device deviceDb = customerDb.getDevice();
//
//		return( deviceDb.getUuid().equalsIgnoreCase(uuid) &&
//				sessionDb.getSid().equalsIgnoreCase(sid) );
//    }
//
//	static public boolean isOnlyLoginAllowed() {
//		return onlyLoginAllowed;
//	}
//
//	static private void setOnlyLoginAllowed(boolean onlyLoginAllowed) {
//		GeneWayRequestFactoryServlet.onlyLoginAllowed = onlyLoginAllowed;
//	}
}
