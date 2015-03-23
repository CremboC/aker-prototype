package uk.ac.sanger.mig.aker.services;

import java.util.Collection;

import uk.ac.sanger.mig.aker.domain.Type;
import uk.ac.sanger.mig.aker.repositories.TypeRepository;

/**
 * @author pi1
 * @since February 2015
 */
public interface TypeService extends RepositoryService<TypeRepository> {

	public Collection<Type> findAll();
}
