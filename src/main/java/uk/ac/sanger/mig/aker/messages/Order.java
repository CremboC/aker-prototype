package uk.ac.sanger.mig.aker.messages;

import java.io.Serializable;

/**
 * @author pi1
 * @since February 2015
 */
public class Order implements Serializable {
	private String message;

	public Order() {

	}

	public Order(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
