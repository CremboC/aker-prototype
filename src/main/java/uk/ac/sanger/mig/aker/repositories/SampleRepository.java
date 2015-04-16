package uk.ac.sanger.mig.aker.repositories;

import java.util.Collection;
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

	@Override
	Collection<Sample> findAll(Iterable<Long> ids);

	Collection<Sample> findAllByIdInAndOwner(Collection<Long> id, String owner);

	Sample findByIdAndOwner(Long id, String owner);

	Page<Sample> findAllByOwner(@Param("owner") String user, Pageable pageable);

	Set<Sample> findAllByGroupsIdIn(Collection<Long> groupId);

	Page<Sample> findAllByTypeValueInAndOwner(Set<String> types, String owner, Pageable pageable);

	Page<Sample> findAllByGroupsIdAndOwner(long groupId, String owner, Pageable pageable);

	// concat is used to convert the id into a string
	@Query("select s from Sample s where concat(s.id) LIKE %:search% and s.owner = :owner")
	Collection<Sample> searchByBarcode(@Param("search") Long sample, @Param("owner") String owner);

	@Query("select s from Sample s join s.aliases as a where lower(a.name) like %:search% and s.owner = :owner")
	Collection<Sample> searchByAlias(@Param("search") String alias, @Param("owner") String owner);

	@Query("select count(distinct s.type) from Sample s where s.id in :samples")
	Integer countDifferentTypes(@Param("samples") Collection<Long> samplesIds);
}
