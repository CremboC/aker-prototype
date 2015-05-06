package uk.ac.sanger.mig.aker.services;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.validation.constraints.NotNull;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import uk.ac.sanger.mig.aker.domain.Sample;
import uk.ac.sanger.mig.aker.domain.Searchable;
import uk.ac.sanger.mig.aker.domain.requests.SampleRequest;

/**
 * @author pi1
 * @since February 2015
 */
public interface SampleService {

	/**
	 * Creates 'Samples' from 'SampleRequest'
	 *
	 * @param request sample request
	 * @param owner
	 */
	List<Sample> createSamples(@NotNull SampleRequest request, String owner);

	/**
	 * Find a sample by its barcode
	 *
	 * @param barcode barcode of the sample
	 * @param owner   owner of the sample
	 * @return a sample, if one is found
	 */
	Optional<Sample> findByBarcode(@NotNull String barcode, String owner);

	/**
	 * Find samples using a set of barcodes
	 *
	 * @param barcode set of barcodes
	 * @param owner   owner of the sample
	 * @return potentially a set of samples
	 */
	Set<Sample> findByBarcodes(Collection<String> barcode, String owner);

	Page<Sample> findAll(Pageable p);

	Collection<Searchable<?>> search(String sample, String owner);
}

