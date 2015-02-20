package uk.ac.sanger.mig.aker.services;

import uk.ac.sanger.mig.aker.messages.Order;

/**
 * @author pi1
 * @since February 2015
 */
public interface OrderService {

	/**
	 * Send an order to a specific RabbitMQ queue
	 *
	 * @param order testing class...
	 */
	public void sendOrder(Order order);

	/**
	 * Handles messages received on a specific RabbitMQ queue
	 *
	 * @param message the message
	 */
	public void receiveConfirmation(String message);

}
