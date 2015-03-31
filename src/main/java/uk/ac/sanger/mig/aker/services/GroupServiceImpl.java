package uk.ac.sanger.mig.aker.services;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;

import org.apache.commons.collections4.IteratorUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import uk.ac.sanger.mig.aker.domain.Group;
import uk.ac.sanger.mig.aker.domain.Sample;
import uk.ac.sanger.mig.aker.domain.requests.GroupRequest;
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

		if (!groupRequest.getSamples().isEmpty()) {
			return groupOfSamples(groupRequest);
		}

		if (!groupRequest.getGroups().isEmpty()) {
			return groupOfGroups(groupRequest);
		}

		throw new IllegalStateException("Samples and groups are empty");
	}

	@Transactional
	@Override
	public Group save(@NotNull Group group) {
		final List<Group> filteredChildren = group.getChildren()
				.stream()
				.filter(Group::isRemove)
				.map(child -> {
					child.setParent(null);
					return child;
				})
				.collect(Collectors.toList());

		repository.save(filteredChildren);

		final Group parent = group.getParent();
		if (parent.getId() == null) {
			group.setParent(null);
		} else {
			group.setParent(repository.findOne(parent.getId()));
		}

		repository.save(group);

		return group;
	}

	@Override
	public GroupRepository getRepository() {
		return repository;
	}

	/**
	 * Handles creation of a group of samples
	 *
	 * @param groupRequest group request with samples
	 * @return a group, if one was successfully created
	 */
	private Optional<Group> groupOfSamples(@NotNull GroupRequest groupRequest) {
		final Set<Sample> allByBarcode = sampleService.findAllByBarcode(groupRequest.getSamples());

		if (!allByBarcode.isEmpty()) {
			Group group = new Group();

			group.setName(groupRequest.getName());
			group.setSamples(allByBarcode);
			group.setType(groupRequest.getType());
			group.setOwner(SecurityContextHolder.getContext().getAuthentication().getName());

			group = repository.save(group);

			return Optional.ofNullable(group);
		}

		throw new IllegalStateException("Non-existing barcodes provided: " + groupRequest.toString());
	}

	/**
	 * Handles creation of a group of groups
	 *
	 * @param groupRequest group request with groups
	 * @return a group, if one was successfully created
	 */
	private Optional<Group> groupOfGroups(@NotNull GroupRequest groupRequest) {
		final Set<Long> groups = groupRequest.getGroups();
		final Set<Group> byParentIdIn = repository.findAllByIdIn(groups);

		if (!byParentIdIn.isEmpty()) {
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
}
