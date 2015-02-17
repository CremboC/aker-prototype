package uk.ac.sanger.mig.proto.aker.seeders;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import uk.ac.sanger.mig.proto.aker.entities.Sample;
import uk.ac.sanger.mig.proto.aker.entities.SampleType;
import uk.ac.sanger.mig.proto.aker.repositories.SampleRepository;
import uk.ac.sanger.mig.proto.aker.repositories.SampleTypeRepository;

/**
 * @author pi1
 * @since February 2015
 */
@Component
public class SampleSeeder {

	@Autowired
	private SampleRepository sampleRepository;

	@Autowired
	private SampleTypeRepository sampleTypeRepository;

	@PostConstruct
	private void seed() {

		sampleRepository.deleteAll();
		sampleTypeRepository.deleteAll();

		SampleType blood = new SampleType();
		blood.setName("blood");

		SampleType dna = new SampleType();
		dna.setName("dna");

		Sample s = new Sample();
		s.setBarcode("WTSI00001");
		s.setType(dna);

		sampleTypeRepository.save(blood);
		sampleTypeRepository.save(dna);

		sampleRepository.save(s);
	}

}
