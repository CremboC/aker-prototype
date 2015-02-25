package uk.ac.sanger.mig.aker.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import uk.ac.sanger.mig.aker.domain.Label;
import uk.ac.sanger.mig.aker.domain.Sample;
import uk.ac.sanger.mig.aker.domain.SampleRequest;
import uk.ac.sanger.mig.aker.domain.Status;
import uk.ac.sanger.mig.aker.domain.Type;
import uk.ac.sanger.mig.aker.repositories.LabelRepository;
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
	private LabelRepository labelRepository;

	@Autowired
	private StatusRepository statusRepository;

	@Override
	public Iterable<Sample> createSamples(@NotNull SampleRequest request) {
		final int amount = request.getAmount();
		final Type type = request.getType();
		final Status pendingStatus = statusRepository.findByValue("pending");

		List<Sample> newSamples = new ArrayList<>(amount);
		List<Label> labels = new ArrayList<>(amount);

		Integer lastId = repository.lastId();
		lastId = lastId == null ? 0 : lastId;

		for (int i = lastId; i < amount + lastId; i++) {
			final Sample s = new Sample();
			s.setType(type);
			s.setBarcode(s.createBarcode(i));
			s.setStatus(pendingStatus);

			final Label l = new Label();
			l.setName("Created Sample");
			l.setMain(true);
			l.setSample(s);

			newSamples.add(s);
			labels.add(l);
		}

		Iterable<Sample> samples = repository.save(newSamples);
		labelRepository.save(labels);

		return samples;
	}

	@Override
	public Optional<Sample> findByBarcode(@NotNull String barcode) {
		final Sample sample = repository.findByBarcode(barcode);

		if (sample != null) {
			final Optional<Label> label = findMainLabel(sample.getLabels());
			if (label.isPresent()) {
				sample.setMainLabel(label.get());

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
	public List<Sample> findAll() {
		return null;
	}

	@Override
	public Page<Sample> findAll(Pageable pageable) {
		final Page<Sample> all = repository.findAll(pageable);

		// set main label for all samples
		all.forEach(s -> {
			final Optional<Label> label = findMainLabel(s.getLabels());
			if (label.isPresent()) {
				s.setMainLabel(label.get());
			}
		});

		return all;
	}

	private Optional<Label> findMainLabel(Set<Label> labels) {
		return labels.stream().filter(l -> l.isMain()).findAny();
	}

}
