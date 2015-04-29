package uk.ac.sanger.mig.aker.domain.requests;

import java.util.Collection;
import java.util.HashSet;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.ToStringBuilder;

import uk.ac.sanger.mig.aker.domain.Type;

/**
 * @author pi1
 * @since February 2015
 */
public class GroupRequest extends SampleGroup {

	@NotNull
	private String name;

	private Collection<Long> groups = new HashSet<>();

	private Type type;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Collection<Long> getGroups() {
		return groups;
	}

	public void setGroups(Collection<Long> groups) {
		this.groups = groups;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this)
				.appendSuper(super.toString())
				.append("name", name)
				.append("groups", groups)
				.append("type", type)
				.toString();
	}
}
