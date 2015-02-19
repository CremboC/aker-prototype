package uk.ac.sanger.mig.proto.aker.services;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import uk.ac.sanger.mig.proto.aker.messages.Order;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author pi1
 * @since February 2015
 */
@Service
public class OrderServiceImpl implements OrderService {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

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
}
