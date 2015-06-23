package com.nutrinfomics.geneway.server;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Locale;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import com.google.web.bindery.requestfactory.server.RequestFactoryServlet;

public class Utils {
    public static Object copy(Object orig) {
        Object obj = null;
        try {
            // Write the object out to a byte array
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(bos);
            out.writeObject(orig);
            out.flush();
            out.close();

            // Make an input stream from the byte array and read
            // a copy of the object back in.
            ObjectInputStream in = new ObjectInputStream(
                new ByteArrayInputStream(bos.toByteArray()));
            obj = in.readObject();
        }
        catch(IOException e) {
            e.printStackTrace();
        }
        catch(ClassNotFoundException cnfe) {
            cnfe.printStackTrace();
        }
        return obj;
    }
    static public Locale getLocale(){
    	HttpServletRequest threadLocalRequest = RequestFactoryServlet.getThreadLocalRequest();
    	if(threadLocalRequest != null){
    		Cookie[] cookies = threadLocalRequest.getCookies();
        	if(cookies != null){
        		for(Cookie cookie : cookies){
            		if(cookie.getName().equals("gwtLocale")) return new Locale(cookie.getValue());
            	}        		
        	}
    	}
    	return new Locale("en");
    }

}
