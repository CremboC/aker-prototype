package uk.ac.sanger.mig.aker.services;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import static java.util.Comparator.*;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import uk.ac.sanger.mig.aker.domain.Group;
import uk.ac.sanger.mig.aker.domain.Sample;
import uk.ac.sanger.mig.aker.domain.Tag;
import uk.ac.sanger.mig.aker.domain.requests.OrderRequest;
import uk.ac.sanger.mig.aker.domain.requests.OrderRequest.OrderOption;
import uk.ac.sanger.mig.aker.domain.requests.OrderRequest.OrderSample;
import uk.ac.sanger.mig.aker.messages.Order;
import uk.ac.sanger.mig.aker.repositories.GroupRepository;
import uk.ac.sanger.mig.aker.repositories.TagRepository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author pi1
 * @since February 2015
 */
@Service
public class WorkOrderServiceImpl implements WorkOrderService {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Resource
	private SampleService sampleService;

	@Resource
	private GroupRepository groupRepository;

	@Resource
	private TagRepository tagRepository;

	@SuppressWarnings("SpringJavaAutowiringInspection")
	@Autowired
	private RabbitTemplate rabbitTemplate;

	@Value("${general.exportPath}")
	private String exportPath;

	@Value("${messaging.queue}")
	private String receivingQueue;

	private Map<String, Sample> sampleMap;
	private List<String> optionNames;

	@Override
	public void sendOrder(Order order) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			rabbitTemplate.convertAndSend(receivingQueue, mapper.writeValueAsString(order));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			logger.error("");
		}
	}

	@Override
	public void receiveConfirmation(String message) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			final Order order = mapper.readValue(message, Order.class);
			System.out.println(order.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void processOrder(OrderRequest order) {
		final String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();

		// fetch all barcodes
		final Set<String> barcodes = order.getSamples()
				.stream()
				.map(OrderSample::getBarcode)
				.collect(Collectors.toSet());

		// query db to get all sample information
		Collection<Sample> samples = new HashSet<>();
		if (!barcodes.isEmpty()) {
			samples = sampleService.byBarcode(barcodes, currentUser);
		}

		// get all samples from groups
		order.getSamples().addAll(groupsToOrderSamples(order.getGroups(), barcodes, samples));

		// convert to a barcode -> sample map for quick access
		sampleMap = samples
				.stream()
				.collect(Collectors.toMap(
						Sample::getBarcode,
						s -> s
				));

		// put all per-sample options names into a list to make check if needed easier
		optionNames = order.getProduct().getOptions()
				.stream()
				.map(OrderOption::getName)
				.collect(Collectors.toList());

		// set tags for all samples
		final List<OrderSample> processedOrderSamples = order.getSamples()
				.stream()
				.sorted(comparing(OrderSample::getBarcode))
				.parallel()
				.map(this::processOrderSample)
				.collect(Collectors.toList());

		order.setSamples(processedOrderSamples);

		// set estimated cost
		final Double unitCost = Optional.ofNullable(order.getProduct().getUnitCost()).orElse(0.0);
		order.setEstimateCost(unitCost * order.getSamples().size());

		order.setProcessed(true);

		sampleMap.clear();
		optionNames.clear();
	}

	@Override
	public File printOrder(OrderRequest order) throws IOException {

		final Set<OrderOption> options = order.getProduct().getOptions();
		String[] headers = new String[options.size()];

		final File outputFile = new File(exportPath + File.separator + "order-work.csv");
		outputFile.getParentFile().mkdirs();

		int i = 0;
		for (final OrderOption option : options) {
			headers[i++] = option.getName();
		}

		Writer output = new FileWriter(outputFile);
		final CSVPrinter csv = new CSVPrinter(output, CSVFormat.DEFAULT);

		//		csv.printRecord(headers);

		for (OrderSample sample : order.getSamples()) {
			final Collection<String> values = sample.getOptions().values();

			Object[] record = new Object[] {
					sample.getBarcode(),
					""
			};

			record = ArrayUtils.addAll(record, values.toArray());

			csv.printRecord(record);
		}

		csv.close();
		output.close();

		return outputFile;
	}

	@Override
	public void update(OrderRequest order) {
		final String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();

		final Set<String> barcodes = order.getSamples().stream().map(OrderSample::getBarcode)
				.collect(Collectors.toSet());

		Collection<Sample> samples = sampleService.byBarcode(barcodes, currentUser);

		Map<String, Sample> sampleMap = samples
				.stream()
				.collect(Collectors.toMap(
						Sample::getBarcode,
						s -> s
				));

		// create and save tags for each sample
		for (OrderSample orderSample : order.getSamples()) {
			Sample sample = sampleMap.get(orderSample.getBarcode());
			Collection<Tag> existingTags = tagRepository.findBySample(sample);

			final Collection<Tag> tags = orderSample
					.getOptions()
					.entrySet()
					.stream()
					.filter(option -> !option.getValue().equals("")) // filter out empty values
					.map(option -> {
						Tag tag = new Tag();

						tag.setName(option.getKey());
						tag.setValue(option.getValue());
						tag.setSample(sampleMap.get(orderSample.getBarcode()));

						return tag;
					})
					.filter(tag -> !existingTags.contains(tag)) // filter tags that already exists
					.collect(Collectors.toList());

			tagRepository.save(tags);
		}
	}

	/**
	 * Process an order sample:
	 * <ul>
	 * <li>Get all samples tags</li>
	 * <li>Insert all missing options (i.e. required for product, but sample has no tag for it)</li>
	 * </ul>
	 *
	 * @param orderSample object to process
	 * @return processed sample
	 */
	private OrderSample processOrderSample(OrderSample orderSample) {
		final Sample sample = sampleMap.get(orderSample.getBarcode());

		// foreach tag found in db, add it to the order sample object with the format name -> value
		sample.getTags()
				.stream()
				.filter(t -> optionNames.contains(t.getName()))
				.forEach(t -> orderSample.getOptions().put(t.getName(), t.getValue()));

		// insert all options which are not set (makes form template simpler)
		final Map<String, String> options = orderSample.getOptions();
		optionNames.stream()
				.filter(optionName -> !options.containsKey(optionName))
				.forEach(optionName -> options.put(optionName, null));

		return orderSample;
	}

	/**
	 * Converts group ids into a list of OrderSample.
	 *
	 * @param groupIds group ids
	 * @param barcodes barcodes to omit – to make sure there are no duplicates
	 * @param samples  sample set
	 * @return list of order samples
	 */
	private Collection<OrderSample> groupsToOrderSamples(Collection<Long> groupIds, Collection<String> barcodes,
			Collection<Sample> samples) {
		Collection<OrderSample> orderSamples = new ArrayList<>();

		final Set<Group> groups = groupRepository.findAllByIdIn(groupIds);

		for (Group group : groups) {
			for (Sample sample : group.getSamples()) {
				// ignore samples we already know about
				if (barcodes.contains(sample.getBarcode())) {
					continue;
				}

				// otherwise add them to the list
				samples.add(sample);

				OrderSample os = new OrderSample();
				os.setBarcode(sample.getBarcode());

				for (Tag tag : sample.getTags()) {
					os.getOptions().put(tag.getName(), tag.getValue());
				}

				orderSamples.add(os);
			}
		}

		return orderSamples;
	}
}
