package uk.ac.sanger.mig.proto.aker.seeders;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import uk.ac.sanger.mig.proto.aker.entities.Sample;
import uk.ac.sanger.mig.proto.aker.entities.Status;
import uk.ac.sanger.mig.proto.aker.entities.Type;
import uk.ac.sanger.mig.proto.aker.repositories.SampleRepository;
import uk.ac.sanger.mig.proto.aker.repositories.StatusRepository;
import uk.ac.sanger.mig.proto.aker.repositories.TypeRepository;

/**
 * @author pi1
 * @since February 2015
 */
@Component
public class SampleSeeder {

	@Autowired
	private SampleRepository sampleRepository;

	@Autowired
	private TypeRepository typeRepository;

	@Autowired
	private StatusRepository statusRepository;

	/**
	 * Seed database with test data
	 */
	public void seed() {

		Status pending = new Status();
		pending.setValue("pending");

		statusRepository.save(pending);

		Status consumed = new Status();
		consumed.setValue("consumed");

		statusRepository.save(consumed);

		Type blood = new Type();
		blood.setName("blood");

		Type dna = new Type();
		dna.setName("dna");

		typeRepository.save(blood);
		typeRepository.save(dna);

		Sample s;
		List<Sample> ss = new ArrayList<>();
		for (int i = 0; i < 10; i++) {
			s = new Sample();

			s.setBarcode(s.createBarcode(i));
			s.setType(dna);
			s.setStatus(pending);

			ss.add(s);
		}
		for (int i = 10; i < 20; i++) {
			s = new Sample();

			s.setBarcode(s.createBarcode(i));
			s.setType(blood);
			s.setStatus(consumed);

			ss.add(s);
		}
		sampleRepository.save(ss);
	}

}
