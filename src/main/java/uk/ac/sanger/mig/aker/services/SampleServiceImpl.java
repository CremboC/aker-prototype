package uk.ac.sanger.mig.aker.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
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
import uk.ac.sanger.mig.aker.domain.Searchable;
import uk.ac.sanger.mig.aker.domain.Status;
import uk.ac.sanger.mig.aker.domain.Type;
import uk.ac.sanger.mig.aker.domain.requests.SampleRequest;
import uk.ac.sanger.mig.aker.repositories.AliasRepository;
import uk.ac.sanger.mig.aker.repositories.SampleRepository;
import uk.ac.sanger.mig.aker.repositories.StatusRepository;
import uk.ac.sanger.mig.aker.utils.SampleHelper;

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

		IntStream.range(0, amount).forEach(i -> {
			final Sample s = new Sample();
			s.setType(type);
			s.setStatus(pendingStatus);
			s.setOwner(SecurityContextHolder.getContext().getAuthentication().getName());

			final Alias l = new Alias();
			l.setName(s.getType().getValue() + " Sample");
			l.setMain(true);
			l.setSample(s);

			newSamples.add(s);
			aliases.add(l);
		});

		Iterable<Sample> samples = repository.save(newSamples);
		aliasRepository.save(aliases);

		return IteratorUtils.toList(samples.iterator());
	}

	@Override
	public Optional<Sample> byBarcode(String barcode, String owner) {
		return Optional.ofNullable(repository.findByIdAndOwner(SampleHelper.idFromBarcode(barcode), owner));
	}

	@Override
	public Collection<Sample> byBarcode(Collection<String> barcodes, String owner) {
		return repository.findAllByIdInAndOwner(SampleHelper.idFromBarcode(barcodes), owner);
	}

	@Override
	public Page<Sample> findAll(Pageable pageable) {
		final String owner = SecurityContextHolder.getContext().getAuthentication().getName();

		return repository.findAllByOwner(owner, pageable);
	}

	@Override
	public Page<Sample> byGroup(long groupId, String owner, Pageable pageable) {
		return repository.findAllByGroupsIdAndOwner(groupId, owner, pageable);
	}

	@Override
	public Page<Sample> byType(Set<String> types, String owner, Pageable pageable) {
		return repository.findAllByTypeValueInAndOwner(types, owner, pageable);
	}

	@Override
	public Collection<Searchable<?>> search(String query, String owner) {
		final Collection<Sample> byBarcode = repository.searchByBarcode(query, owner);
		final Collection<Sample> byAlias = repository.searchByAlias(query, owner);

		// merges two collections into a single list
		final List<Sample> merged = Stream
				.of(byBarcode, byAlias)
				.flatMap(Collection::stream)
				.collect(Collectors.toList());

		return merged.stream().collect(Collectors.toList());
	}

	@Override
	public SampleRepository getRepository() {
		return repository;
	}
}
