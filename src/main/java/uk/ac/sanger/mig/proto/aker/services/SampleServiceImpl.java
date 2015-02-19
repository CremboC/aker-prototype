package uk.ac.sanger.mig.proto.aker.services;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.ac.sanger.mig.proto.aker.entities.Sample;
import uk.ac.sanger.mig.proto.aker.entities.SampleRequest;
import uk.ac.sanger.mig.proto.aker.entities.Status;
import uk.ac.sanger.mig.proto.aker.entities.Type;
import uk.ac.sanger.mig.proto.aker.repositories.SampleRepository;
import uk.ac.sanger.mig.proto.aker.repositories.StatusRepository;

/**
 * @author pi1
 * @since February 2015
 */
@Service
public class SampleServiceImpl implements SampleService {

	@Autowired
	private SampleRepository repository;

	@Autowired
	private StatusRepository statusRepository;

	@Override
	public void createSamples(@NotNull SampleRequest request) {
		final int amount = request.getAmount();
		final Type type = request.getType();
		final Status pendingStatus = statusRepository.findByValue("pending");

		List<Sample> newSamples = new ArrayList<>(amount);

		final int lastId = repository.lastId();
		for (int i = lastId; i < amount + lastId; i++) {
			final Sample s = new Sample();
			s.setType(type);
			s.setBarcode(s.createBarcode(i));
			s.setStatus(pendingStatus);
			s.setName("Created Sample");
			newSamples.add(s);
		}

		repository.save(newSamples);
	}

}
