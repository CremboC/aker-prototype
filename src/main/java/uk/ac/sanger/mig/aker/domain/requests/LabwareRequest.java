package uk.ac.sanger.mig.aker.domain.requests;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * A group of samples for labware
 *
 * @author pi1
 * @since March 2015
 */
public class LabwareRequest extends SampleGroup {

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
