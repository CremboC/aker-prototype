package uk.ac.sanger.mig.proto.aker.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import uk.ac.sanger.mig.proto.aker.entities.Sample;

/**
 * @author pi1
 * @since February 2015
 */
@Repository
public interface SampleRepository extends CrudRepository<Sample, Long> {

	public Sample findOneByBarcode(String barcode);

	public List<Sample> findByBarcode(String barcode);

	public List<Sample> findByTypeId(long id);
}