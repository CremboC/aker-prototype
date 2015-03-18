package uk.ac.sanger.mig.aker.services;

import java.io.IOException;
import java.security.Principal;

/**
 * @author pi1
 * @since March 2015
 */
public interface OrderService {

	/**
	 * Query a source for JSON of orders
	 *
	 * @param principal current user
	 * @return JSON of orders
	 */
	String queryOrders(Principal principal) throws IOException;

	String queryOrder(Long id, Principal principal) throws IOException;
}
