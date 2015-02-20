package uk.ac.sanger.mig.aker.services;

import java.util.List;

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
	public Iterable<Type> findAll() {
		final List<Type> all = (List<Type>) typeRepository.findAll();
		all.forEach(type -> type.setName(WordUtils.capitalizeFully(type.getName())));
		return all;
	}
}