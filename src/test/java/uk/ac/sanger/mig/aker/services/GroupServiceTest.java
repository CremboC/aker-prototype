package uk.ac.sanger.mig.aker.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.transaction.Transactional;

import static java.util.Comparator.*;
import static java.util.stream.Collectors.*;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import uk.ac.sanger.mig.aker.Application;
import uk.ac.sanger.mig.aker.domain.Group;
import uk.ac.sanger.mig.aker.domain.Sample;
import uk.ac.sanger.mig.aker.domain.Searchable;
import uk.ac.sanger.mig.aker.domain.Type;
import uk.ac.sanger.mig.aker.domain.requests.GroupRequest;
import uk.ac.sanger.mig.aker.repositories.GroupRepository;
import uk.ac.sanger.mig.aker.repositories.TypeRepository;
import uk.ac.sanger.mig.aker.utils.SampleHelper;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class GroupServiceTest {

	@Autowired
	private GroupService groupService;

	@Autowired
	private TypeRepository typeRepository;

	private Type testType;
	private final String user = "test";

	@Before
	public void setUp() throws Exception {
		testType = typeRepository.findOne(1L);
	}

	@Transactional
	@Test
	public void testCreateGroupOfGroups() throws Exception {
		GroupRequest gr = new GroupRequest();

		Collection<Long> groups = new ArrayList<>();
		groups.add(1L);
		groups.add(2L);

		gr.setType(testType);
		gr.setName("Test Creation");
		gr.setGroups(groups);

		Optional<Group> maybeGroup = groupService.createGroup(gr, user);

		assertTrue(maybeGroup.isPresent());

		Group group = maybeGroup.get();

		assertEquals(testType, group.getType());
		assertEquals("Test Creation", group.getName());

		Collection<Group> children = group.getSubgroups();
		Collection<Long> ids = children.stream().map(Group::getId).collect(toList());

		assertEquals(groups, ids);
	}

	@Transactional
	@Test
	public void testCreateGroupOfSamples() throws Exception {
		GroupRequest gr = new GroupRequest();

		Collection<String> samples = new ArrayList<>();
		samples.add(SampleHelper.barcodeFromId(1, Sample.BARCODE_SIZE));
		samples.add(SampleHelper.barcodeFromId(2, Sample.BARCODE_SIZE));
		samples.add(SampleHelper.barcodeFromId(3, Sample.BARCODE_SIZE));
		samples.add(SampleHelper.barcodeFromId(4, Sample.BARCODE_SIZE));

		gr.setType(testType);
		gr.setName("Test Creation of Samples");
		gr.setSamples(samples);

		Optional<Group> maybeGroup = groupService.createGroup(gr, user);

		assertTrue(maybeGroup.isPresent());

		Group group = maybeGroup.get();

		assertEquals(testType, group.getType());
		assertEquals("Test Creation of Samples", group.getName());

		Collection<String> barcodes = group.getSamples().stream().map(Sample::getBarcode).collect(toList());

		assertEquals(samples, barcodes);
	}

	@Transactional
	@Test
	public void testSearch() throws Exception {
		Group group = new Group();
		group.setName("Test Search");
		group.setType(testType);
		group.setOwner(user);
		groupService.getRepository().save(group);

		Collection<Searchable<?>> searchables = groupService.search("Test Search", user);

		assertTrue(searchables.size() == 1);

		Searchable<?> searchable = searchables.stream().findFirst().get();

		assertTrue(searchable instanceof Group);

		Group foundGroup = (Group) searchable;
		assertEquals(group.getName(), foundGroup.getName());
		assertEquals(group.getType(), foundGroup.getType());
	}

	@Test
	public void testSamplesFromGroups() throws Exception {
		Collection<Long> groups = new ArrayList<>();
		groups.add(1L);
		groups.add(2L);

		Collection<Long> samplesOfGroups = new ArrayList<>();
		samplesOfGroups.add(1L);
		samplesOfGroups.add(2L);
		samplesOfGroups.add(3L);
		samplesOfGroups.add(4L);
		samplesOfGroups.add(5L);

		Set<Sample> samples = groupService.samplesFromGroups(groups);
		List<Long> sampleIds = samples.stream().sorted(comparing(Sample::getId)).map(Sample::getId).collect(toList());

		assertEquals(samplesOfGroups, sampleIds);
	}

	@Test
	public void testValidParents() throws Exception {
		GroupRepository repository = groupService.getRepository();
		Group group = repository.findOne(1L);
		Group subgroup = repository.findOne(3L);
		Group parent = repository.findOne(4L);

		Collection<Group> availableParents = new HashSet<>();
		availableParents.add(repository.findOne(2L));
		availableParents.add(repository.findOne(5L));
		availableParents.add(parent); // current parent is also a valid parent

		Set<Group> groups = groupService.validParents(group, user);

		Set<Long> expectedGroupIds = availableParents.stream().map(Group::getId).collect(toSet());
		Set<Long> actualGroupIds = groups.stream().map(Group::getId).collect(toSet());

		assertFalse("One of the valid parents is the subgroup. This shouldn't be the case", actualGroupIds.contains(subgroup.getId()));
		assertTrue("Expected " + expectedGroupIds + "; Actual: " + actualGroupIds, actualGroupIds.containsAll(expectedGroupIds));
	}

	@Test
	public void testValidSubgroups() throws Exception {
		GroupRepository repository = groupService.getRepository();
		Group subject = repository.findOne(1L);
		Group subgroup = repository.findOne(3L);
		Group parent = repository.findOne(4L);

		Collection<Group> otherGroups = new ArrayList<>();
		otherGroups.add(repository.findOne(2L));
		otherGroups.add(repository.findOne(5L));
		otherGroups.add(repository.findOne(6L));
		otherGroups.add(repository.findOne(7L));

		Collection<Group> validSubgroups = groupService.validSubgroups(subject, user);

		assertFalse("Valid subgroups shouldn't contains group itself", validSubgroups.contains(subject));
		assertFalse("Valid subgroups shouldn't contain parent", validSubgroups.contains(parent));
		assertFalse("Valid subgroups shouldn't contain a current subgroup.", validSubgroups.contains(subgroup));

		Set<Long> expectedGroupIds = otherGroups.stream().map(Group::getId).collect(toSet());
		Set<Long> actualGroupIds = validSubgroups.stream().map(Group::getId).collect(toSet());

		assertTrue("Expected " + expectedGroupIds + "; Actual: " + actualGroupIds, actualGroupIds.containsAll(expectedGroupIds));
	}

	@Transactional
	@Test
	public void testGetGroupType() throws Exception {
		GroupRequest gr = new GroupRequest();

		Collection<String> samples = new ArrayList<>();
		samples.add(SampleHelper.barcodeFromId(1, Sample.BARCODE_SIZE));
		samples.add(SampleHelper.barcodeFromId(2, Sample.BARCODE_SIZE));
		samples.add(SampleHelper.barcodeFromId(3, Sample.BARCODE_SIZE));
		samples.add(SampleHelper.barcodeFromId(4, Sample.BARCODE_SIZE));

		gr.setName("Test Creation of Samples");
		gr.setSamples(samples);

		Optional<Type> maybeGroupType = groupService.getGroupType(gr, user);

		assertTrue("Group type must exist", maybeGroupType.isPresent());

		Type type = maybeGroupType.get();

		assertEquals("Type mismatch", testType, type);
	}
}
