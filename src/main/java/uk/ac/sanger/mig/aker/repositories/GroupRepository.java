package uk.ac.sanger.mig.aker.repositories;

import java.util.Collection;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import uk.ac.sanger.mig.aker.domain.Group;
import uk.ac.sanger.mig.aker.domain.Sample;

/**
 * @author pi1
 * @since February 2015
 */
@Repository
public interface GroupRepository extends PagingAndSortingRepository<Group, Long> {

	Page<Group> findAllByOwner(@Param("owner") String user, Pageable pageable);

	Set<Group> findByParentId(long parentId);

	Set<Group> findAllByIdIn(Collection<Long> groups);

	Collection<Group> findAllByIdNotAndOwner(Long group, String owner);

	Page<Sample> findAllByTypeValueInAndOwner(Set<String> types, String owner, Pageable pageable);
}
