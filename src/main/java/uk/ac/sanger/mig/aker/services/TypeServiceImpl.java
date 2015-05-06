package uk.ac.sanger.mig.aker.services;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.ac.sanger.mig.aker.domain.Type;
import uk.ac.sanger.mig.aker.repositories.TypeRepository;

/**
 * @author pi1
 * @since February 2015
 */
@Service
public class TypeServiceImpl implements TypeService {

	@Autowired
	private TypeRepository typeRepository;

	@Override
	public Collection<Type> findAll() {
		return typeRepository.findAll();
	}
}
