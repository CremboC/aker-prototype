package uk.ac.sanger.mig.aker.services;

import java.util.Collection;

import uk.ac.sanger.mig.aker.domain.Type;

/**
 * @author pi1
 * @since February 2015
 */
public interface TypeService {
	Collection<Type> findAll();
}
