package uk.ac.sanger.mig.proto.aker.services;

import javax.validation.constraints.NotNull;

import uk.ac.sanger.mig.proto.aker.entities.SampleRequest;

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
