package uk.ac.sanger.mig.aker.services;

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

		if (groupRequest.getSamples() != null) {
			final Set<Sample> allByBarcode = sampleService.findAllByBarcode(groupRequest.getSamples());

			if (allByBarcode != null && !allByBarcode.isEmpty()) {
				Group group = new Group();

				group.setName(groupRequest.getName());
				group.setSamples(allByBarcode);

				return repository.save(group);
			}

			throw new IllegalStateException("Non-existing barcodes provided: " + groupRequest.toString());
		}

		if (groupRequest.getGroups() != null) {
			System.out.println(groupRequest.getGroups());
			final Set<Group> byParentIdIn = repository.findAllByIdIn(groupRequest.getGroups());

			if (byParentIdIn != null && !byParentIdIn.isEmpty()) {
				Group group = new Group();
				group.setName(groupRequest.getName());
				group = repository.save(group);

				for (Group subGroup : byParentIdIn) {
					subGroup.setParent(group);
				}

				repository.save(byParentIdIn);

				return group;
			}

			throw new IllegalStateException("No groups specified in the request found: " + groupRequest.toString());
		}

		throw new IllegalStateException("Samples and groups are empty");
	}

	@Override
	public Page<Group> findAll(Pageable pageable) {
		return repository.findAll(pageable);
	}
}
