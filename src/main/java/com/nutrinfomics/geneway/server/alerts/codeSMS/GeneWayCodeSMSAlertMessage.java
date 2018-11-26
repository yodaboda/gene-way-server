package com.nutrinfomics.geneway.server.alerts.codeSMS;


public class GeneWayCodeSMSAlertMessage implements com.geneway.alerts.AlertMessage {

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
