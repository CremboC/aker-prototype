package uk.ac.sanger.mig.aker.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;

import uk.ac.sanger.mig.aker.domain.User;

/**
 * @author pi1
 * @since February 2015
 */
public interface UserRepository extends PagingAndSortingRepository<User, Long> {
}
