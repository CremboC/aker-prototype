package uk.ac.sanger.mig.aker.services;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import uk.ac.sanger.mig.aker.utils.JsonUtils;
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
	public Collection<Object> queryOrders(String owner) {
		final Optional<String> parse = UrlUtils.parse(uri + owner);

		return JsonUtils.toCollection(parse.orElse(""));
	}

	@Override
	public Map<String, Object> queryOrder(Long id, String owner) {
		final Optional<String> parse = UrlUtils.parse(uri + owner + "/" + id);

		return JsonUtils.toMap(parse.orElse(""));
	}
}
