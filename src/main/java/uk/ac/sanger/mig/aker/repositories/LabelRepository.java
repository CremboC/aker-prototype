package uk.ac.sanger.mig.aker.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import uk.ac.sanger.mig.aker.domain.Label;

/**
 * @author pi1
 * @since February 2015
 */
@Repository
public interface LabelRepository extends CrudRepository<Label, Long> {

}
