package uk.ac.sanger.mig.aker.services;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.validation.constraints.NotNull;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import uk.ac.sanger.mig.aker.domain.Sample;
import uk.ac.sanger.mig.aker.domain.SampleRequest;
import uk.ac.sanger.mig.aker.domain.Searchable;

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
	public List<Sample> createSamples(@NotNull SampleRequest request);

	/**
	 * Find a sample by its barcode
	 *
	 * @param barcode barcode of the sample
	 * @return a sample, if one is found
	 */
	public Optional<Sample> findByBarcode(@NotNull String barcode);

	/**
	 * Find samples using a set of barcodes
	 *
	 * @param barcode set of barcodes
	 * @return potentially a set of samples
	 */
	public Optional<Set<Sample>> findAllByBarcode(Set<String> barcode);

	public Page<Sample> findAll(Pageable p);

	public Collection<Searchable<?>> search(String sample);
}
