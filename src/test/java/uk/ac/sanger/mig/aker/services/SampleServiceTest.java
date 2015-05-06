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
import uk.ac.sanger.mig.aker.domain.Sample;
import uk.ac.sanger.mig.aker.domain.Searchable;
import uk.ac.sanger.mig.aker.domain.Status;
import uk.ac.sanger.mig.aker.domain.Type;
import uk.ac.sanger.mig.aker.domain.requests.SampleRequest;
import uk.ac.sanger.mig.aker.repositories.SampleRepository;
import uk.ac.sanger.mig.aker.repositories.StatusRepository;
import uk.ac.sanger.mig.aker.repositories.TypeRepository;
import uk.ac.sanger.mig.aker.utils.SampleHelper;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class SampleServiceTest {

	@Autowired
	private SampleService sampleService;

	@Autowired
	private SampleRepository sampleRepository;

	@Autowired
	private TypeRepository typeRepository;

	@Autowired
	private StatusRepository statusRepository;

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
		String requestUser = "test-create-samples";

		Type requestType = new Type();
		requestType.setValue("Request Test");
		requestType = typeRepository.save(requestType);

		int amount = 100;

		SampleRequest sampleRequest = new SampleRequest();
		sampleRequest.setType(requestType);
		sampleRequest.setAmount(amount);

		sampleService.createSamples(sampleRequest, requestUser);

		List<Sample> samples = sampleRepository.findAllByOwner(requestUser, new PageRequest(0, amount + 1)).getContent();

		long sampleTypes = samples.stream().map(Sample::getType).distinct().count();

		assertEquals("There should be only a single type of samples", 1L, sampleTypes);
		assertEquals("Amount is incorrect, expected: " + amount + ". Actual: " + samples.size(), amount, samples.size());
	}

	@Transactional
	@Test
	public void testSearchByAlias() throws Exception {
		String alias = "search sample";

		Collection<Searchable<?>> results = sampleService.search(alias, user);

		assertEquals(1, results.size());

		Searchable<?> searchable = results.stream().findFirst().get();

		assertTrue(searchable instanceof Sample);

		Sample foundSample = (Sample) searchable;

		assertEquals(testType, foundSample.getType());
		assertTrue(foundSample.getMainAlias().getName().toLowerCase().contains(alias.toLowerCase()));
		assertEquals(user, foundSample.getOwner());
	}

	@Transactional
	@Test
	public void testSearchByBarcode() throws Exception {
		String id = "7";

		Collection<Searchable<?>> results = sampleService.search(id, user);

		assertEquals(1, results.size());

		Searchable<?> searchable = results.stream().findFirst().get();

		assertTrue(searchable instanceof Sample);

		Sample foundSample = (Sample) searchable;

		assertEquals(testType, foundSample.getType());
		assertEquals(SampleHelper.barcodeFromId(Long.parseLong(id), Sample.BARCODE_SIZE), foundSample.getBarcode());
		assertEquals(user, foundSample.getOwner());
	}
}
