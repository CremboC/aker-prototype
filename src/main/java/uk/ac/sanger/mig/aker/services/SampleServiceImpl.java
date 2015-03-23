package uk.ac.sanger.mig.aker.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.validation.constraints.NotNull;

import org.apache.commons.collections4.IteratorUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import uk.ac.sanger.mig.aker.domain.Alias;
import uk.ac.sanger.mig.aker.domain.Sample;
import uk.ac.sanger.mig.aker.domain.SampleRequest;
import uk.ac.sanger.mig.aker.domain.Searchable;
import uk.ac.sanger.mig.aker.domain.Status;
import uk.ac.sanger.mig.aker.domain.Type;
import uk.ac.sanger.mig.aker.repositories.AliasRepository;
import uk.ac.sanger.mig.aker.repositories.SampleRepository;
import uk.ac.sanger.mig.aker.repositories.StatusRepository;

/**
 * @author pi1
 * @since February 2015
 */
@Service
public class SampleServiceImpl implements SampleService {

	@Autowired
	private SampleRepository repository;

	@Autowired
	private AliasRepository aliasRepository;

	@Autowired
	private StatusRepository statusRepository;

	@Override
	public List<Sample> createSamples(@NotNull SampleRequest request) {
		final int amount = request.getAmount();
		final Type type = request.getType();
		final Status pendingStatus = statusRepository.findByValue("pending");

		Collection<Sample> newSamples = new ArrayList<>(amount);
		Collection<Alias> aliases = new ArrayList<>(amount);

		Integer lastId = repository.lastId();
		lastId = lastId == null ? 0 : lastId;

		for (int i = lastId; i < amount + lastId; i++) {
			final Sample s = new Sample();
			s.setType(type);
			s.setBarcode(s.createBarcode(i));
			s.setStatus(pendingStatus);
			s.setOwner(SecurityContextHolder.getContext().getAuthentication().getName());

			final Alias l = new Alias();
			l.setName("Created Sample");
			l.setMain(true);
			l.setSample(s);

			newSamples.add(s);
			aliases.add(l);
		}

		Iterable<Sample> samples = repository.save(newSamples);
		aliasRepository.save(aliases);

		return IteratorUtils.toList(samples.iterator());
	}

	@Override
	public Optional<Sample> findByBarcode(@NotNull String barcode) {
		final Sample sample = repository.findByBarcode(barcode);

		if (sample != null) {
			final Optional<Alias> alias = findMainAlias(sample.getAliases());
			if (alias.isPresent()) {
				sample.setMainAlias(alias.get());

				return Optional.of(sample);
			}
		}

		return Optional.empty();
	}

	@Override
	public Set<Sample> findAllByBarcode(Set<String> barcode) {
		return repository.findAllByBarcodeIn(barcode);
	}

	@Override
	public Page<Sample> findAll(Pageable pageable) {
		final String owner = SecurityContextHolder.getContext().getAuthentication().getName();
		final Page<Sample> all = repository.findAllByOwner(owner, pageable);

		// set main label for all samples
		all.forEach(this::setMainAlias);

		return all;
	}

	@Override
	public Collection<Searchable<?>> search(String query) {
		final String owner = SecurityContextHolder.getContext().getAuthentication().getName();

		final Collection<Sample> byBarcode = repository.searchByBarcode(query, owner);
		final Collection<Sample> byAlias = repository.searchByAlias(query, owner);

		final List<Sample> allResults = Stream
				.of(byBarcode, byAlias)
				.flatMap(Collection::stream)
				.collect(Collectors.toList());

		allResults.forEach(this::setMainAlias);

		return allResults.stream().collect(Collectors.toList());
	}

	private void setMainAlias(Sample sample) {
		final Alias mainAlias = findMainAlias(sample.getAliases()).orElseThrow(IllegalStateException::new);
		sample.setMainAlias(mainAlias);
	}

	private Optional<Alias> findMainAlias(Collection<Alias> aliases) {
		return aliases.stream().filter(Alias::isMain).findAny();
	}

	@Override
	public SampleRepository getRepository() {
		return repository;
	}
}
