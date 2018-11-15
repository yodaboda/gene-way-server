package com.nutrinfomics.geneway.server.alert.format;

import java.util.Locale;
import java.util.ResourceBundle;

import com.nutrinfomics.geneway.server.ResourceBundles;
import com.nutrinfomics.geneway.shared.SnackProperty;

public class SnackPropertyFormat {

	private static SnackPropertyFormat instance;
	
	public static SnackPropertyFormat getInstance(){
		if(instance == null){
			synchronized (SnackPropertyFormat.class) {
				if(instance == null){
					instance = new SnackPropertyFormat();
				}
			}
		}
		return instance;
	}
	
	private SnackPropertyFormat(){
	}

	public String format(SnackProperty snackProperty, Locale locale){
		return ResourceBundles.getMiscResource(snackProperty.toString(), locale);
	}
	
}
