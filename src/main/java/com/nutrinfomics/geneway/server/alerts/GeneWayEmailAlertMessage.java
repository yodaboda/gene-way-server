package com.nutrinfomics.geneway.server.alerts;

import com.geneway.alerts.message.EmailAlertMessage;

public class GeneWayEmailAlertMessage extends EmailAlertMessage {
	@Override
	public String getSubject() {
		return "itsTimeToTakeYourMealTitle";
	}

	@Override
	public String[] getBody() {
		return new String[]{"itsTimeToTakeYourMeal"};
	}

}
