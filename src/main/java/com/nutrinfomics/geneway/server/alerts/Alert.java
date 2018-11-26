package com.nutrinfomics.geneway.server.alerts;

/**
 * Alerts are used to remind users of pending actions.
 * @author Firas Swidan
 *
 */
public interface Alert {
	/**
	 * Remind user of a pending event.
	 */
	public void remind();
}
