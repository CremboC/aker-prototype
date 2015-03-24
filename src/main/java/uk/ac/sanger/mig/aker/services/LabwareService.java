package uk.ac.sanger.mig.aker.services;

import java.util.Map;
import java.util.Optional;

/**
 * @author pi1
 * @since March 2015
 */
public interface LabwareService {

	Optional<String> queryAll(String owner);

	Optional<String> queryOne(String owner, String identifier);

	Map<String, Object> queryTypes();

	Map<String, Object> querySizes();

	Optional<Map<String, String>> querySize(String name);

}
