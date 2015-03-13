package uk.ac.sanger.mig.aker.services;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.validation.constraints.NotNull;

import org.apache.commons.collections4.IteratorUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
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
	public Optional<Group> createGroup(@NotNull GroupRequest groupRequest) {

		if (groupRequest.getSamples() != null) {
			final Optional<Set<Sample>> allByBarcode = sampleService.findAllByBarcode(groupRequest.getSamples());

			if (allByBarcode.isPresent()) {
				Group group = new Group();

				group.setName(groupRequest.getName());
				group.setSamples(allByBarcode.get());
				group.setType(groupRequest.getType());
				group.setOwner(SecurityContextHolder.getContext().getAuthentication().getName());

				group = repository.save(group);

				if (group != null) {
					return Optional.of(group);
				} else {
					return Optional.empty();
				}
			}

			throw new IllegalStateException("Non-existing barcodes provided: " + groupRequest.toString());
		}

		final Set<Long> groups = groupRequest.getGroups();
		if (groups != null) {
			final Set<Group> byParentIdIn = repository.findAllByIdIn(groups);

			if (byParentIdIn != null && !byParentIdIn.isEmpty()) {
				Group group = new Group();
				group.setName(groupRequest.getName());
				group.setOwner(SecurityContextHolder.getContext().getAuthentication().getName());
				group = repository.save(group);

				for (Group subGroup : byParentIdIn) {
					subGroup.setParent(group);
				}
				final List<Group> saved = IteratorUtils.toList(repository.save(byParentIdIn).iterator());

				if (group.getId() > 1 && !saved.isEmpty()) {
					return Optional.of(group);
				}

				return Optional.empty();
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
