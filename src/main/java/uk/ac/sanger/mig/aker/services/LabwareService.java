package uk.ac.sanger.mig.aker.services;

import java.util.Map;
import java.util.Optional;

import uk.ac.sanger.mig.aker.domain.external.LabwareSize;
import uk.ac.sanger.mig.aker.domain.external.LabwareType;

/**
 * @author pi1
 * @since March 2015
 */
public interface LabwareService {

	Optional<String> findAll(String owner);

	Optional<String> findOne(String owner, String identifier);

	Map<String, Object> findAllTypes();

	Map<String, Object> findAllSizes();

	LabwareSize findOneSize(String name);

	LabwareType findOneType(String name);

}
