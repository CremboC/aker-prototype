package uk.ac.sanger.mig.aker.services;

import java.io.File;
import java.io.IOException;

import uk.ac.sanger.mig.aker.domain.requests.OrderRequest;
import uk.ac.sanger.mig.aker.messages.Order;

/**
 * @author pi1
 * @since February 2015
 */
public interface WorkOrderService {

	/**
	 * Send an order to a specific RabbitMQ queue
	 *
	 * @param order testing class...
	 */
	void sendOrder(Order order);

	/**
	 * Handles messages received on a specific RabbitMQ queue
	 *
	 * @param message the message§
	 */
	void receiveConfirmation(String message);

	/**
	 * Process an order in the following manner: gets all relevant tags (options).
	 *
	 * @param order a work order partially filled
	 */
	OrderRequest processOrder(OrderRequest order);

	/**
	 * Prints an order into a CSV format
	 *
	 * @param order to print
	 */
	File printOrder(OrderRequest order) throws IOException;

	/**
	 * Save an order locally:
	 * <ul>
	 * <li>Update sample tags/options</li>
	 * </ul>
	 *
	 * @param order
	 */
	void update(OrderRequest order);
}
