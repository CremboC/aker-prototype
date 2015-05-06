package uk.ac.sanger.mig.aker.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import uk.ac.sanger.mig.aker.domain.Status;

/**
 * @author pi1
 * @since February 2015
 */
@Repository
public interface StatusRepository extends JpaRepository<Status, Long> {
	Status findByValue(String value);
}
