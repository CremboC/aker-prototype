package uk.ac.sanger.mig.aker.services;

import java.util.List;
import java.util.Optional;

import javax.validation.constraints.NotNull;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import uk.ac.sanger.mig.aker.domain.Sample;
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

	/**
	 * Find a sample by its barcode
	 *
	 * @param barcode barcode of the label
	 * @return a label, if one is found
	 */
	public Optional<Sample> findByBarcode(@NotNull String barcode);

	public List<Sample> findAll();

	public Page<Sample> findAll(Pageable pageable);

}
