package uk.ac.sanger.mig.aker.domain;

import java.util.Set;

import javax.validation.constraints.NotNull;

/**
 * @author pi1
 * @since February 2015
 */
public class GroupRequest {

	@NotNull
	private String name;

	@NotNull
	private Set<String> samples;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Set<String> getSamples() {
		return samples;
	}

	public void setSamples(Set<String> samples) {
		this.samples = samples;
	}
}
