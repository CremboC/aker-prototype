package uk.ac.sanger.mig.aker.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import uk.ac.sanger.mig.aker.domain.Sample;

/**
 * @author pi1
 * @since February 2015
 */
@Repository
public interface SampleRepository extends PagingAndSortingRepository<Sample, Long> {

	public Sample findByBarcode(String barcode);

	public List<Sample> findByTypeId(long id);

	@Query("select max(id) from Sample")
	public Integer lastId();
}