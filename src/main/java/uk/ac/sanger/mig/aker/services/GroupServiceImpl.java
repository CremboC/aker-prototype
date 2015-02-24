package uk.ac.sanger.mig.aker.services;

import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import uk.ac.sanger.mig.aker.domain.Group;
import uk.ac.sanger.mig.aker.domain.GroupRequest;
import uk.ac.sanger.mig.aker.domain.Sample;
import uk.ac.sanger.mig.aker.repositories.GroupRepository;

/**
 * @author pi1
 * @since February 2015
 */
@Service
public class GroupServiceImpl implements GroupService {

	@Autowired
	private GroupRepository repository;

	@Autowired
	private SampleService sampleService;

	@Override
	public Group createGroup(GroupRequest groupRequest) {
		final Optional<Set<Sample>> allByBarcode = sampleService.findAllByBarcode(groupRequest.getSamples());

		if (allByBarcode.isPresent()) {
			final Set<Sample> samples = allByBarcode.get();
			Group group = new Group();

			group.setName(groupRequest.getName());
			group.setSamples(samples);

			return repository.save(group);
		}

		throw new IllegalStateException("Non-existing barcodes provided");
	}

	@Override
	public Page<Group> findAll(Pageable pageable) {
		return repository.findAll(pageable);
	}
}
