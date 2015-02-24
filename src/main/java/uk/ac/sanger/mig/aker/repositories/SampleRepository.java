package uk.ac.sanger.mig.aker.repositories;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
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

	@Query("select max(s.id) from Sample s")
	public Integer lastId();

	public Set<Sample> findAllByBarcodeIn(Set<String> barcode);

	@Query("select s from Sample s join s.groups g where g.id = :groupId")
	public Page<Sample> byGroupId(@Param("groupId") long groupId, Pageable pageable);

}