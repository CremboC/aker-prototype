package uk.ac.sanger.mig.aker.services;

import java.util.Collection;
import java.util.List;

import javax.transaction.Transactional;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import uk.ac.sanger.mig.aker.Application;
import uk.ac.sanger.mig.aker.domain.Alias;
import uk.ac.sanger.mig.aker.domain.Sample;
import uk.ac.sanger.mig.aker.domain.Searchable;
import uk.ac.sanger.mig.aker.domain.Status;
import uk.ac.sanger.mig.aker.domain.Type;
import uk.ac.sanger.mig.aker.domain.requests.SampleRequest;
import uk.ac.sanger.mig.aker.repositories.AliasRepository;
import uk.ac.sanger.mig.aker.repositories.StatusRepository;
import uk.ac.sanger.mig.aker.repositories.TypeRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class SampleServiceTest {

	@Autowired
	private SampleService sampleService;

	@Autowired
	private TypeRepository typeRepository;

	@Autowired
	private StatusRepository statusRepository;

	@Autowired
	private AliasRepository aliasRepository;

	private Type testType;
	private Status testStatus;
	private final String user = "test-samples";

	@Before
	public void setUp() throws Exception {
		testType = typeRepository.findOne(1L);
		testStatus = statusRepository.findOne(1L);
	}

	@Transactional
	@Test
	public void testCreateSamples() throws Exception {
		int amount = 100;

		SampleRequest sampleRequest = new SampleRequest();
		sampleRequest.setType(testType);
		sampleRequest.setAmount(amount);

		sampleService.createSamples(sampleRequest, user);

		List<Sample> samples = sampleService.getRepository().findAllByOwner(user, new PageRequest(0, amount + 1)).getContent();

		long sampleTypes = samples.stream().map(Sample::getType).distinct().count();

		assertEquals("There should be only a single type of samples", 1L, sampleTypes);
		assertEquals("Amount is incorrect, expected: " + amount + ". Actual: " + samples.size(), amount, samples.size());
	}

	@Transactional
	@Test
	public void testSearch() throws Exception {
		String name = "Test Search Sample";
		Alias mainAlias = new Alias(name);

		Sample sample = new Sample();
		sample.setType(testType);
		sample.setOwner(user);
		sample.setStatus(testStatus);
		sample = sampleService.getRepository().save(sample);

		mainAlias.setSample(sample);

		aliasRepository.save(mainAlias);

		Collection<Searchable<?>> results = sampleService.search(name, user);

		assertEquals(1, results.size());

		Searchable<?> searchable = results.stream().findFirst().get();

		assertTrue(searchable instanceof Sample);

		Sample foundSample = (Sample) searchable;

		assertEquals(testType, foundSample.getType());
		assertEquals(mainAlias, foundSample.getMainAlias());
		assertEquals(user, foundSample.getOwner());
	}
}
