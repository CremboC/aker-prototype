package uk.ac.sanger.mig.aker.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.stereotype.Repository;

import uk.ac.sanger.mig.aker.domain.Status;

/**
 * @author pi1
 * @since February 2015
 */
@Repository
@RestResource(path = "status")
public interface StatusRepository extends CrudRepository<Status, Long> {
	Status findByValue(String value);
}
