package uk.ac.sanger.mig.aker.services;

import javax.validation.constraints.NotNull;

import uk.ac.sanger.mig.aker.domain.SampleRequest;

/**
 * @author pi1
 * @since February 2015
 */
public interface SampleService {

	/**
	 * Creates 'Samples' from 'SampleRequest'
	 *
	 * @param request sample request
	 */
	public void createSamples(@NotNull SampleRequest request);

}
