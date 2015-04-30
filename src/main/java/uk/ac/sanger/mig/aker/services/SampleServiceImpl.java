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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import uk.ac.sanger.mig.aker.domain.Sample;
import uk.ac.sanger.mig.aker.domain.Searchable;
import uk.ac.sanger.mig.aker.domain.Status;
import uk.ac.sanger.mig.aker.domain.Type;
import uk.ac.sanger.mig.aker.domain.requests.SampleRequest;
import uk.ac.sanger.mig.aker.repositories.SampleRepository;
import uk.ac.sanger.mig.aker.repositories.StatusRepository;
import uk.ac.sanger.mig.aker.utils.SampleHelper;

/**
 * @author pi1
 * @since February 2015
 */
@Service
public class SampleServiceImpl implements SampleService {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private SampleRepository repository;

	@Autowired
	private StatusRepository statusRepository;

	@Override
	public List<Sample> createSamples(@NotNull SampleRequest request) {
		final int amount = request.getAmount();
		final Type type = request.getType();
		final Status pendingStatus = statusRepository.findByValue("pending");
		final String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();

		Collection<Sample> newSamples = new ArrayList<>(amount);

		IntStream.range(0, amount).forEach(i -> {
			final Sample s = new Sample();
			s.setType(type);
			s.setStatus(pendingStatus);
			s.setOwner(currentUser);

			newSamples.add(s);
		});

		Iterable<Sample> samples = repository.save(newSamples);

		return IteratorUtils.toList(samples.iterator());
	}

	@Override
	public Optional<Sample> byBarcode(String barcode, String owner) {
		return Optional.ofNullable(repository.findByIdAndOwner(SampleHelper.idFromBarcode(barcode), owner));
	}

	@Override
	public Set<Sample> byBarcode(Collection<String> barcodes, String owner) {
		return repository.findAllByIdInAndOwner(SampleHelper.idFromBarcode(barcodes), owner);
	}

	@Override
	public Page<Sample> findAll(Pageable pageableRequest) {
		final String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();

		Pageable pageable = new PageRequest(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), new Sort(
				new Sort.Order(Sort.Direction.DESC, "id")
		));

		return repository.findAllByOwner(currentUser, pageable);
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
		Collection<Sample> byBarcode = new ArrayList<>();
		try {
			byBarcode = repository.searchByBarcode(Long.parseLong(query), owner);
		} catch (NumberFormatException e) {
			if (logger.isDebugEnabled()) {
				e.printStackTrace();
			}
			logger.error(e.getMessage());
		}

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
