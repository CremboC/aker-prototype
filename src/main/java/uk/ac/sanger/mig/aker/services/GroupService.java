package uk.ac.sanger.mig.aker.services;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import uk.ac.sanger.mig.aker.domain.Group;
import uk.ac.sanger.mig.aker.domain.Sample;
import uk.ac.sanger.mig.aker.domain.Searchable;
import uk.ac.sanger.mig.aker.domain.requests.GroupRequest;
import uk.ac.sanger.mig.aker.repositories.GroupRepository;

/**
 * @author pi1
 * @since February 2015
 */
public interface GroupService extends RepositoryService<GroupRepository> {

	/**
	 * Create a group from a group request
	 *
	 * @param groupRequest group request with mandatory elements set
	 * @return saved group
	 */
	Optional<Group> createGroup(GroupRequest groupRequest);

	Optional<Group> save(Group group);

	Collection<Searchable<?>> search(String query, String name);

	/**
	 * Extracts all samples from a collection of group ids
	 *
	 * @param ids    group ids
	 * @return set of samples
	 */
	Set<Sample> samplesFromGroups(Collection<Long> ids);

	boolean delete(Long id, String owner);

	Page<Group> allByOwner(String owner, Pageable pageable);
}
