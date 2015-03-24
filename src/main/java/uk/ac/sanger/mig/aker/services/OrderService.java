package uk.ac.sanger.mig.aker.services;

import java.io.IOException;
import java.util.Optional;

/**
 * @author pi1
 * @since March 2015
 */
public interface OrderService {

	/**
	 * Query a source for JSON of orders
	 *
	 * @param owner current user
	 * @return JSON of orders
	 */
	Optional<String> queryOrders(String owner);

	/**
	 * Query details of a single order
	 *
	 * @param id an identifier as defined by the orders microservice
	 * @param owner the owner, to crosscheck.
	 * @return a JSON  of the order
	 * @throws IOException upon failure to reach the microservice
	 */
	Optional<String> queryOrder(Long id, String owner);
}
