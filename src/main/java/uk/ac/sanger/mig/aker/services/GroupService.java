package uk.ac.sanger.mig.aker.services;

import java.util.Optional;

import uk.ac.sanger.mig.aker.domain.Group;
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
	public Optional<Group> createGroup(GroupRequest groupRequest);

}
