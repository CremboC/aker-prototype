package uk.ac.sanger.mig.aker.domain.external;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Represents objects that come from external sources as JSON or other similar format and should be mapped.
 * Due to the fact that we sometimes not need all
 *
 * @author pi1
 * @since March 2015
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class ExternalMappable {

}
