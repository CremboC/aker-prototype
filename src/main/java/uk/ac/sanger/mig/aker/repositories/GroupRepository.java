package uk.ac.sanger.mig.aker.repositories;

import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import uk.ac.sanger.mig.aker.domain.Group;
import uk.ac.sanger.mig.aker.domain.Sample;

/**
 * @author pi1
 * @since February 2015
 */
@Repository
public interface GroupRepository extends PagingAndSortingRepository<Group, Long> {

	public Set<Group> findByParentId(long parentId);

	public Set<Group> findAllByParentIdIn(Long... groupIds);

	public Set<Group> findAllByIdIn(Long... groups);

	public Set<Group> findAllByIdNotIn(Long... groups);

	public Page<Sample> findAllByTypeValueIn(Set<String> types, Pageable pageable);
}
