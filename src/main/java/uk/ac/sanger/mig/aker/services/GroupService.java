package uk.ac.sanger.mig.aker.services;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

import javax.validation.constraints.NotNull;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import uk.ac.sanger.mig.aker.domain.Group;
import uk.ac.sanger.mig.aker.domain.Sample;
import uk.ac.sanger.mig.aker.domain.Searchable;
import uk.ac.sanger.mig.aker.domain.Type;
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
	 * @param owner
	 * @return saved group
	 */
	Optional<Group> createGroup(GroupRequest groupRequest, String owner);

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

	/**
	 * Gets groups which can be either a parent or a subgroup
	 *
	 * @param group the group subject
	 * @param owner owner of groups
	 * @return legal groups
	 */
	Collection<Group> otherGroups(Group group, String owner);

	Optional<Type> getGroupType(@NotNull GroupRequest groupRequest, String owner);
}
