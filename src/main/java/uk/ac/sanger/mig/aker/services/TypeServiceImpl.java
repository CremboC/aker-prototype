package uk.ac.sanger.mig.aker.services;

import java.util.List;

import org.apache.commons.collections4.IteratorUtils;
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
	public List<Type> findAll() {
		final Iterable<Type> all = typeRepository.findAll();
		all.forEach(type -> type.setValue(WordUtils.capitalizeFully(type.getValue())));
		return IteratorUtils.toList(all.iterator());
	}
}