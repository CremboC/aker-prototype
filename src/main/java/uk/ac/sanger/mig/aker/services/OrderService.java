package uk.ac.sanger.mig.aker.services;

import java.io.IOException;

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
	String queryOrders(String owner) throws IOException;

	/**
	 * Query details of a single order
	 *
	 * @param id an identifier as defined by the orders microservice
	 * @param owner the owner, to crosscheck.
	 * @return a JSON  of the order
	 * @throws IOException upon failure to reach the microservice
	 */
	String queryOrder(Long id, String owner) throws IOException;
}
