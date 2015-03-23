package uk.ac.sanger.mig.aker.services;

import java.io.Serializable;

import org.springframework.data.repository.CrudRepository;

/**
 * @author pi1
 * @since March 2015
 */
public interface RepositoryService<T extends CrudRepository<?, ? extends Serializable>> {

	/**
	 * @return get the repository of this service
	 */
	public T getRepository();

}
