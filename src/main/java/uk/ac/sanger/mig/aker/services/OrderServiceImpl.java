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
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import uk.ac.sanger.mig.aker.domain.Group;
import uk.ac.sanger.mig.aker.domain.OrderRequest;
import uk.ac.sanger.mig.aker.domain.OrderRequest.OrderOption;
import uk.ac.sanger.mig.aker.domain.OrderRequest.OrderSample;
import uk.ac.sanger.mig.aker.domain.Sample;
import uk.ac.sanger.mig.aker.domain.Tag;
import uk.ac.sanger.mig.aker.messages.Order;
import uk.ac.sanger.mig.aker.repositories.GroupRepository;
import uk.ac.sanger.mig.aker.repositories.SampleRepository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author pi1
 * @since February 2015
 */
@Service
public class OrderServiceImpl implements OrderService {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Resource
	private SampleRepository sampleRepository;

	@Resource
	private GroupRepository groupRepository;

	@SuppressWarnings("SpringJavaAutowiringInspection")
	@Autowired
	private RabbitTemplate rabbitTemplate;

	@Value("${general.exportPath}")
	private String exportPath;

	@Value("${messaging.queue}")
	private String receivingQueue;

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

		}
	}

	@Override
	public void processOrder(OrderRequest order) {
		// fetch all barcodes
		final Set<String> barcodes = order.getSamples()
				.stream()
				.map(b -> b.getBarcode())
				.collect(Collectors.toSet());

		// query db to get all sample information
		Set<Sample> samples = new HashSet<>();
		if (!barcodes.isEmpty()) {
			samples = sampleRepository.findAllByBarcodeIn(barcodes);
		}

		// get all samples from groups
		order.getSamples().addAll(groupsToOrderSamples(order.getGroups(), barcodes, samples));

		// convert to a barcode -> sample map for quick access
		final Map<String, Sample> sampleMap = samples
				.stream()
				.collect(Collectors.toMap(
						s -> s.getBarcode(),
						s -> s
				));

		// put all per-sample options names into a list to make check if needed easier
		final List<String> optionNames = order.getProduct().getOptions()
				.stream()
				.map(o -> o.getName())
				.collect(Collectors.toList());

		// set tags for all samples
		for (OrderSample orderSample : order.getSamples()) {
			final Sample sample = sampleMap.get(orderSample.getBarcode());

			// foreach tag found in db, add it to the order sample object with the format name -> value
			sample.getTags()
					.stream()
					.forEach(tag -> {
						if (optionNames.contains(tag.getName())) {
							orderSample.getOptions().put(tag.getName(), tag.getValue());
						}
					});

			// insert all options which are not set (makes form template simpler)
			optionNames.stream()
					.filter(optionName -> !orderSample.getOptions().containsKey(optionName))
					.forEach(optionName -> orderSample.getOptions().put(optionName, null));

		}

		// finally sort samples by barcode
		order.getSamples().sort((s1, s2) -> s1.getBarcode().compareTo(s2.getBarcode()));

		// set estimated cost
		order.setEstimateCost(order.getProduct().getUnitCost() * order.getSamples().size());

		order.setProcessed(true);
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

	/**
	 * Converts group ids into a list of OrderSample.
	 *
	 * @param groupIds group ids
	 * @param barcodes barcodes to omit – to make sure there are no duplicates
	 * @param samples  sample set
	 * @return list of order samples
	 */
	private List<OrderSample> groupsToOrderSamples(List<Long> groupIds, Set<String> barcodes, Set<Sample> samples) {
		List<OrderSample> orderSamples = new ArrayList<>();

		final Set<Group> groups = groupRepository.findAllByIdIn(groupIds);

		for (Group group : groups) {
			for (Sample sample : group.getSamples()) {
				if (barcodes.contains(sample.getBarcode())) {
					continue;
				}

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
