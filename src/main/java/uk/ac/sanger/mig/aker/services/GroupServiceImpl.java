package uk.ac.sanger.mig.aker.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;

import org.apache.commons.collections4.IteratorUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import uk.ac.sanger.mig.aker.domain.Group;
import uk.ac.sanger.mig.aker.domain.Sample;
import uk.ac.sanger.mig.aker.domain.Searchable;
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

	@Transactional
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
	public Optional<Group> save(@NotNull Group group) {

		// remove subgroups
		final Collection<Group> filteredChildren = group.getChildren()
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

		// remove samples
		final Set<Sample> filteredSamples = group.getSamples()
				.stream()
				.filter(sample -> sample.getBarcode() != null)
				.collect(Collectors.toSet());

		group.setSamples(filteredSamples);

		repository.save(group);

		return Optional.of(group);
	}

	@Override
	public Collection<Searchable<?>> search(String query, String owner) {
		final Collection<Group> groups = repository.searchByName(query, owner);
		return new ArrayList<>(groups);
	}

	@Override
	public boolean delete(Long id, String owner) {
		final Group group = repository.findOne(id);

		if (group == null || !group.getOwner().equals(owner)) {
			// illegal condition, shouldn't even be possible so no need to handle this gently
			return false;
		}

		repository.delete(id);

		return true;
	}

	@Override
	public Page<Group> allByOwner(String owner, Pageable pageable) {

		Pageable p = new PageRequest(pageable.getPageNumber(), pageable.getPageSize(), new Sort(
				new Sort.Order(Sort.Direction.DESC, "id")
		));

		final Page<Group> groups = repository.findAllByOwner(owner, p);

		// remove parent's parent to simplify things for conversion into json..
		StreamSupport.stream(groups.spliterator(), false) // get stream
				.filter(g -> g.getParent() != null) // filter groups with no parent
				.forEach(g -> g.getParent().setParent(null)); // set parent's parent to null

		return groups;
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
		final String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();

		final Set<Sample> allByBarcode = sampleService.byBarcode(groupRequest.getSamples(), currentUser);

		if (!allByBarcode.isEmpty()) {
			Group group = new Group();

			group.setName(groupRequest.getName());
			group.setSamples(allByBarcode);
			group.setType(groupRequest.getType());
			group.setOwner(currentUser);

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
		final String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
		final Collection<Long> groups = groupRequest.getGroups();
		final Set<Group> subGroups = repository.findAllByIdIn(groups);

		if (!subGroups.isEmpty()) {
			Group group = new Group();
			group.setName(groupRequest.getName());
			group.setOwner(currentUser);
			group = repository.save(group);

			for (Group subGroup : subGroups) {
				subGroup.setParent(group);
			}
			final List<Group> saved = IteratorUtils.toList(repository.save(subGroups).iterator());

			if (group.getId() > 1 && !saved.isEmpty()) {
				return Optional.of(group);
			}

			return Optional.empty();
		}

		throw new IllegalStateException("No groups specified in the request found: " + groupRequest.toString());
	}
}
