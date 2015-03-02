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

	private Set<String> samples = null;

	// if group request is of groups
	private Set<Long> groups = null;

	private Type type;

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

	public Set<Long> getGroups() {
		return groups;
	}

	public void setGroups(Set<Long> groups) {
		this.groups = groups;
	}

	@Override
	public String toString() {
		return "GroupRequest{" +
				"name='" + name + '\'' +
				", samples=" + samples +
				", groups=" + groups +
				'}';
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}
}
