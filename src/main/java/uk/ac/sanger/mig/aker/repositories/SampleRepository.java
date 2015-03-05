package uk.ac.sanger.mig.aker.repositories;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

	public Set<Sample> findAllByBarcodeIn(Collection<String> barcode);

	public Set<Sample> findAllByGroupsIdIn(Collection<Long> groupId);

	public Page<Sample> findAllByGroupsIdIn(long groupId, Pageable pageable);

	public Page<Sample> findAllByTypeValueIn(Set<String> types, Pageable pageable);

	@Query("select max(s.id) from Sample s")
	public Integer lastId();

}