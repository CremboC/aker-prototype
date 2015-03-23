package uk.ac.sanger.mig.aker.services;

import java.util.Collection;

import org.apache.commons.lang3.text.WordUtils;
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
		final Collection<Type> types = typeRepository.findAll();
		types.forEach(type -> type.setValue(WordUtils.capitalizeFully(type.getValue())));
		return types;
	}

	@Override
	public TypeRepository getRepository() {
		return typeRepository;
	}
}