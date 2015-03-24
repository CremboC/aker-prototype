package uk.ac.sanger.mig.aker.services;

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
@PropertySource("properties/labware.properties")
public class LabwareServiceImpl implements LabwareService {

	@Value("${uri.host}")
	private String uri;

	@Value("${uri.sizes}")
	private String sizesSuffix;

	@Value("${uri.types}")
	private String typesSuffix;

	@Override
	public Optional<String> queryAll(String owner) {
		return UrlUtils.parse(uri);
	}

	@Override
	public Optional<String> queryOne(String owner, String identifier) {
		return null;
	}

	@Override
	public Map<String, Object> queryTypes() {
		final Optional<String> maybeTypes = UrlUtils.parse(uri + typesSuffix);
		return JsonUtils.toMap(maybeTypes.orElse(""));
	}

	@Override
	public Map<String, Object> querySizes() {
		final Optional<String> maybeSizes = UrlUtils.parse(uri + sizesSuffix);
		return JsonUtils.toMap(maybeSizes.orElse(""));
	}

	@Override
	public Optional<Map<String, String>> querySize(String name) {
		return null;
	}
}
