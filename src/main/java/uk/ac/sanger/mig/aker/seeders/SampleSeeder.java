package uk.ac.sanger.mig.aker.seeders;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import uk.ac.sanger.mig.aker.domain.SampleRequest;
import uk.ac.sanger.mig.aker.domain.Status;
import uk.ac.sanger.mig.aker.domain.Type;
import uk.ac.sanger.mig.aker.repositories.SampleRepository;
import uk.ac.sanger.mig.aker.repositories.StatusRepository;
import uk.ac.sanger.mig.aker.repositories.TypeRepository;
import uk.ac.sanger.mig.aker.services.SampleService;

/**
 * @author pi1
 * @since February 2015
 */
@Component
public class SampleSeeder {

	@Autowired
	private SampleRepository sampleRepository;

	@Autowired
	private SampleService sampleService;

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
	}

	public void seedSamples() {
		SampleRequest srDna = new SampleRequest();
		srDna.setAmount(50);
		srDna.setType(typeRepository.findOne(2L));

		sampleService.createSamples(srDna);

		SampleRequest srBlood = new SampleRequest();
		srBlood.setAmount(50);
		srBlood.setType(typeRepository.findOne(1L));

		sampleService.createSamples(srBlood);
	}

}
