package uk.ac.sanger.mig.aker.requests;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * @author pi1
 * @since March 2015
 */
public abstract class Grouping {

	protected Set<String> samples = new HashSet<>();

	public Set<String> getSamples() {
		return samples;
	}

	public void setSamples(Set<String> samples) {
		this.samples = samples;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this)
				.append("samples", samples)
				.toString();
	}
}
