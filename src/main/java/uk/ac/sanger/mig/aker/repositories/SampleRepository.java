package uk.ac.sanger.mig.aker.repositories;

import java.util.Collection;
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

	Page<Sample> findAllByOwner(@Param("owner") String user, Pageable pageable);

	Sample findByBarcode(String barcode);

	List<Sample> findByTypeId(long id);

	Set<Sample> findAllByBarcodeIn(Collection<String> barcode);

	Set<Sample> findAllByGroupsIdIn(Collection<Long> groupId);

	Page<Sample> findAllByTypeValueIn(Set<String> types, Pageable pageable);

	Page<Sample> findAllByTypeValueInAndOwner(Set<String> types, String owner, Pageable pageable);

	Set<Sample> findAllByBarcodeInAndOwner(Collection<String> barcodes, String name);

	Page<Sample> findAllByGroupsId(long groupId, Pageable pageable);

	@Query("select max(s.id) from Sample s")
	Integer lastId();

	@Query("select s from Sample s where s.barcode like %:search% and s.owner = :owner")
	Collection<Sample> searchByBarcode(@Param("search") String sample, @Param("owner") String owner);

	@Query("select s from Sample s join s.aliases as a where lower(a.name) like %:search% and s.owner = :owner")
	Collection<Sample> searchByAlias(@Param("search") String alias, @Param("owner") String owner);

	@Query("select count(distinct s.type) from Sample s where s.barcode in :samples")
	Integer countDifferentTypes(@Param("samples") Collection<String> samples);
}
