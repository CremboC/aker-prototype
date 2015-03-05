package uk.ac.sanger.mig.aker.services;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import uk.ac.sanger.mig.aker.domain.Sample;
import uk.ac.sanger.mig.aker.domain.WorkOrder;
import uk.ac.sanger.mig.aker.messages.Order;
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

	@SuppressWarnings("SpringJavaAutowiringInspection")
	@Autowired
	private RabbitTemplate rabbitTemplate;

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

	@RabbitListener(queues = "test")
	@Override
	public void receiveConfirmation(String message) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			final Order order = mapper.readValue(message, Order.class);
			System.out.println(order.getMessage());
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
	}

	@Override
	public WorkOrder processOrder(WorkOrder order) {

		// fetch all barcodes
		final List<String> barcodes = order.getSamples()
				.stream()
				.map(b -> b.getBarcode())
				.collect(Collectors.toList());

		// query db to get all sample information
		final Set<Sample> samples = sampleRepository.findAllByBarcodeIn(barcodes);

		// convert to a barcode -> sample map for quick access
		final Map<String, Sample> sampleMap = samples
				.stream()
				.collect(Collectors.toMap(
						s -> s.getBarcode(),
						s -> s
				));

		final Set<WorkOrder.OrderOption> options = order.getProduct().getOptions();
		final List<String> optionNames = options.stream().map(o -> o.getName()).collect(Collectors.toList());

		// set tags for all samples
		for (WorkOrder.OrderSample orderSample : order.getSamples()) {
			final Sample sample = sampleMap.get(orderSample.getBarcode());

			// foreach tag found in db, add it to the order sample object with the format name -> value
			sample.getTags()
					.stream()
					.forEach(tag -> {
						if (optionNames.contains(tag.getName())) {
							orderSample.getOptions().put(tag.getName(), tag.getValue());
						}
					});

		}

		order.setProcessed(true);
		return null;
	}
}
