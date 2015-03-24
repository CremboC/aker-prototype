package uk.ac.sanger.mig.aker.requests;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;

import uk.ac.sanger.mig.aker.domain.Type;

/**
 * @author pi1
 * @since February 2015
 */
public class SampleRequest {

	@NotNull
	@Digits(integer = 99999, fraction = 0)
	private int amount;

	@NotNull
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
