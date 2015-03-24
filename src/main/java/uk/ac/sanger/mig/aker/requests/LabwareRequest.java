package uk.ac.sanger.mig.aker.requests;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * @author pi1
 * @since March 2015
 */
public class LabwareRequest extends Grouping {

	@NotNull
	private String type;

	@NotNull
	private String size;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this)
				.appendSuper(super.toString())
				.append("type", type)
				.append("size", size)
				.toString();
	}
}
