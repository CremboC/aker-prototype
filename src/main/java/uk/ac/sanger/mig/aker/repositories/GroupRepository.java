package uk.ac.sanger.mig.aker.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import uk.ac.sanger.mig.aker.domain.Group;

/**
 * @author pi1
 * @since February 2015
 */
@Repository
public interface GroupRepository extends PagingAndSortingRepository<Group, Long> {
}
