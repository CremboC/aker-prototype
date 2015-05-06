package uk.ac.sanger.mig.aker.repositories;

import java.util.Collection;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import uk.ac.sanger.mig.aker.domain.Group;
import uk.ac.sanger.mig.aker.domain.Sample;

/**
 * @author pi1
 * @since February 2015
 */
@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {

	Page<Group> findAllByOwner(@Param("owner") String user, Pageable pageable);

	Set<Group> findByParentId(long parentId);

	Set<Group> findAllByIdIn(Collection<Long> groups);

	Collection<Group> findAllByIdNotInAndOwner(Collection<Long> group, String owner);

	Page<Sample> findAllByTypeValueInAndOwner(Set<String> types, String owner, Pageable pageable);

	@Query("select g from Group g where name like %:query% and owner = :owner")
	Collection<Group> searchByName(@Param("query") String query, @Param("owner") String owner);
}
