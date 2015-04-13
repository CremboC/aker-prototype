package uk.ac.sanger.mig.aker.repositories;

import java.util.Collection;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import uk.ac.sanger.mig.aker.domain.Sample;
import uk.ac.sanger.mig.aker.domain.Tag;

/**
 * @author pi1
 * @since March 2015
 */
@Repository
public interface TagRepository extends PagingAndSortingRepository<Tag, Long> {
	Collection<Tag> findBySample(Sample sample);
}
