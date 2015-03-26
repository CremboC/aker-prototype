package uk.ac.sanger.mig.aker.domain.requests;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Abstracts a grouping of samples
 *
 * @author pi1
 * @since March 2015
 */
public abstract class SampleGroup {

	protected Collection<String> samples = new ArrayList<>();

	public Collection<String> getSamples() {
		return samples;
	}

	public void setSamples(Collection<String> samples) {
		this.samples = samples;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this)
				.append("samples", samples)
				.toString();
	}
}
