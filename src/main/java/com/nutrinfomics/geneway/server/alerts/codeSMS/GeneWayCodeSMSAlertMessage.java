package com.nutrinfomics.geneway.server.alerts.codeSMS;

import com.geneway.alerts.message.AlertMessage;

public class GeneWayCodeSMSAlertMessage implements AlertMessage {

	private String[] body;
	
	public GeneWayCodeSMSAlertMessage(String nickName, String code){
		this.body = new String[]{nickName, code};
	}
	
	@Override
	public String[] getBody() {
		return body;
	}

	@Override
	public String getSubject() {
		return "code";
	}

}
