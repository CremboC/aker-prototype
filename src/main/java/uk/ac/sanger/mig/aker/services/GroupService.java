package uk.ac.sanger.mig.aker.services;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import uk.ac.sanger.mig.aker.domain.Group;
import uk.ac.sanger.mig.aker.domain.GroupRequest;

/**
 * @author pi1
 * @since February 2015
 */
public interface GroupService {

	/**
	 * Create a group from a group request
	 *
	 * @param groupRequest group request with mandatory elements set
	 * @return saved group
	 */
	public Optional<Group> createGroup(GroupRequest groupRequest);

	public Page<Group> findAll(Pageable pageable);

}
