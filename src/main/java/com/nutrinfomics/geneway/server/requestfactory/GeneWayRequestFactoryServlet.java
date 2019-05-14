/*
 * Copyright 2019 Firas Swidan†
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.nutrinfomics.geneway.server.requestfactory;

import javax.inject.Inject;

import com.google.inject.Singleton;
import com.google.web.bindery.requestfactory.server.ExceptionHandler;
import com.google.web.bindery.requestfactory.server.RequestFactoryServlet;
import com.google.web.bindery.requestfactory.server.ServiceLayerDecorator;

@Singleton
public class GeneWayRequestFactoryServlet extends RequestFactoryServlet {

  //	static private boolean onlyLoginAllowed = false;

  @Inject
  public GeneWayRequestFactoryServlet(
      final ExceptionHandler exceptionHandler,
      final ServiceLayerDecorator guiceSL,
      SecurityLocalizationDecorator securityLD) {
    super(exceptionHandler, guiceSL, securityLD);
  }

  //	public GeneWayRequestFactoryServlet(){
  //		super(new DefaultExceptionHandler(), new SecurityDecorator());
  //	}

  //	@Override
  //    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException,
  // ServletException{
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
  //    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws
  // ServletException, IOException{
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

/*
 * †Dr Firas Swidan, PhD. frsswdn@gmail.com. firas.swidan@icloud.com.
 * https://www.linkedin.com/in/swidan
 * POBox  8125,  Nazareth  16480, Israel.
 * Public key: AAAAB3NzaC1yc2EAAAADAQABAAACAQD6Lt98LolwuA/aOcK0h91ECdeiyG3QKcUOT/CcMEPV64cpkv3jrLLGoag7YtzESZ3j7TLEd0WHZ/BZ9d+K2kRfzuuCdMMhrBwqP3YObbTbSIM6NjUNwbH403LLb3FuYApUt1EvC//w64UMm7h3fTo0vdyVuMuGnkRZuM6RRAXcODM4tni9ydd3ZQKN4inztkeH/sOoM77FStk8E2VYbljUQdY39zlRoZwUqNdKzwD3T2G00tmROlTZ6K5L8i68Zqt6s0XNS6XQvS3zXe0fI6UwuetnDrcVr1Yb8y2T8lfjMG9+9L2aKPoUOlOMMcyqM+oKVvRUOSdrzmtKOljnYC7TqzvsKrfXHvHlqHxxhPp1K7B/YWrHwCDbqp02dXdIaXkkHCIqKFNaY06HEWt4obDxppVhC8IabSb55LQVCCT7J4TFbwp6rID2+Y1L7NEvR3v3oaWSlQIZ+WSG04mwh9/7gRCt7XUoqmEXCCPoHqZXq5sWv193XA57pD5gKoX7Rf2i6UdbduNTMIhQMqcWIaPMBFwxUv/LRQCHnS+mlW2GnIHIHHGS/S46MurZ6BMvcb7fEz/NorVxvh3DbUaVTteMYcikH0y5sPmGECB1d99ENBBSEX6diI+PneFp2sOouQ6gOBWy6WAt3spGfLTOFMPo3bMV/UpktkQPpXkmfd1esQ==
 */