package com.nutrinfomics.geneway.server;

import java.util.Locale;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.LocaleUtils;

import com.google.web.bindery.requestfactory.server.RequestFactoryServlet;

public class Utils {
	private static final Logger LOGGER = LogManager.getLogger();
    /**
     * Gets the locale from the HTTP request.
     * @return HTTP request locale or English otherwise
     */
    public Locale getLocale(RequestUtils requestUtils){
    	HttpServletRequest threadLocalRequest = requestUtils.getHttpServletRequest();
    	if(threadLocalRequest != null){
    		Cookie[] cookies = threadLocalRequest.getCookies();
        	if(cookies != null){
        		for(Cookie cookie : cookies){
            		if(cookie.getName().equals("gwtLocale")){
            			try{
            				return LocaleUtils.toLocale(cookie.getValue());
            			}
            			catch(IllegalArgumentException ex){
            				LOGGER.log( Level.FATAL, ex.toString(), ex );
            			}
            		}
            	}        		
        	}
        	else{
        		LOGGER.log(Level.WARN, "Http Request has null cookies!");
        	}
    	}
    	else{
    		LOGGER.log(Level.WARN, "Null Http Request!");
    	}
    	return Locale.ENGLISH;
    }
    /**
     * Gets the IP address from the HTTP request
     * @return client IP address
     */
    public String getIP(RequestUtils requestUtils){
    	HttpServletRequest threadLocalRequest = requestUtils.getHttpServletRequest();
    	if(threadLocalRequest != null){
    		return threadLocalRequest.getRemoteAddr();
    	}
    	else{
    		LOGGER.log(Level.WARN, "Null Http Request!");    		
    	}
    	return null;
    }
    
}
