package uk.ac.sanger.mig.aker.domain.requests;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Abstracts a grouping of unique, distinct sample barcodes
 *
 * @author pi1
 * @since March 2015
 */
public class SampleGroup {

	protected Collection<String> samples = new ArrayList<>();

	public Collection<String> getSamples() {
		return samples;
	}

	public void setSamples(Collection<String> samples) {
		this.samples = samples.stream().distinct().collect(Collectors.toSet());
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this)
				.append("samples", samples)
				.toString();
	}
}
