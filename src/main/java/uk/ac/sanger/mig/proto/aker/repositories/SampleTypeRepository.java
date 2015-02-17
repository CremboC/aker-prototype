package uk.ac.sanger.mig.proto.aker.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import uk.ac.sanger.mig.proto.aker.entities.SampleType;

/**
 * @author pi1
 * @since February 2015
 */
@Repository
public interface SampleTypeRepository extends CrudRepository<SampleType, Long> {


}
