package uk.ac.sanger.mig.aker.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.stereotype.Repository;

import uk.ac.sanger.mig.aker.domain.Type;

/**
 * @author pi1
 * @since February 2015
 */
@Repository
@RestResource(path = "types")
public interface TypeRepository extends PagingAndSortingRepository<Type, Long> {

}
