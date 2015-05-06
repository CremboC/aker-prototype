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

/**
 * @author pi1
 * @since February 2015
 */
public interface GroupService {

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
	 * @param ids group ids
	 * @return set of samples
	 */
	Set<Sample> samplesFromGroups(Collection<Long> ids);

	boolean delete(Long id, String owner);

	Page<Group> allByOwner(String owner, Pageable pageable);

	/**
	 * Groups which can be assigned as a subgroup to the supplied Group.
	 * Everything beside the current parent, the group itself and current subgroups.
	 *
	 * @param group group to look at
	 * @param owner the owner
	 * @return potential valid subgroups
	 */
	Set<Group> validSubgroups(Group group, String owner);

	/**
	 * Groups which can be assigned as a parent to the supplied Group.
	 * Essentially everything beside the current subgroups and itself.
	 * The current parent is a valid option because a group can only have a single parent.
	 * If the current parent is chosen, nothing will be changed.
	 *
	 * @param group group to look at
	 * @param owner the owner
	 * @return potential valid parent groups
	 */
	Set<Group> validParents(Group group, String owner);

	Optional<Type> getGroupType(@NotNull GroupRequest groupRequest, String owner);
}
