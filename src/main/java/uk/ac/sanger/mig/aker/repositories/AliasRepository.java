package uk.ac.sanger.mig.aker.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import uk.ac.sanger.mig.aker.domain.Alias;

/**
 * @author pi1
 * @since February 2015
 */
@Repository
public interface AliasRepository extends JpaRepository<Alias, Long> {

	List<Alias> findBySampleIdAndMain(String sampleId, boolean main);

}
