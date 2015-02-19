package uk.ac.sanger.mig.proto.aker.entities;

/**
 * @author pi1
 * @since February 2015
 */
public class SampleRequest {

	private int amount;
	private Type type;

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}
}
