package uk.ac.sanger.mig.aker.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import uk.ac.sanger.mig.aker.utils.UrlUtils;

/**
 * @author pi1
 * @since March 2015
 */
@Service
@PropertySource("properties/orders.properties")
public class OrderServiceImpl implements OrderService {

	@Value("${uri}")
	private String uri;

	@Override
	public Optional<String> queryOrders(String owner) {
		return UrlUtils.parse(uri + owner);
	}

	@Override
	public Optional<String> queryOrder(Long id, String owner) {
		return UrlUtils.parse(uri + owner + "/" + id);
	}
}
