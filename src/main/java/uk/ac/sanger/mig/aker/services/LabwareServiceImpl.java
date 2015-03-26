package uk.ac.sanger.mig.aker.services;

import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import uk.ac.sanger.mig.aker.domain.external.LabwareSize;
import uk.ac.sanger.mig.aker.domain.external.LabwareType;
import uk.ac.sanger.mig.aker.utils.JsonUtils;
import uk.ac.sanger.mig.aker.utils.UrlUtils;

/**
 * @author pi1
 * @since March 2015
 */
@Service
@PropertySource("classpath:properties/labware.properties")
public class LabwareServiceImpl implements LabwareService {

	@Value("${uri.host}")
	private String uri;

	@Value("${uri.sizes}")
	private String sizesSuffix;

	@Value("${uri.types}")
	private String typesSuffix;

	@Override
	public Optional<String> findAll(String owner) {
		return UrlUtils.parse(uri);
	}

	@Override
	public Optional<String> findOne(String owner, String identifier) {
		return null;
	}

	@Override
	public Map<String, Object> findAllTypes() {
		final Optional<String> maybeTypes = UrlUtils.parse(uri + typesSuffix);
		return JsonUtils.toMap(maybeTypes.orElse(""));
	}

	@Override
	public Map<String, Object> findAllSizes() {
		final Optional<String> maybeSizes = UrlUtils.parse(uri + sizesSuffix);
		return JsonUtils.toMap(maybeSizes.orElse(""));
	}

	@Override
	public LabwareSize findOneSize(String name) {
		final Optional<String> maybeSize = UrlUtils.parse(uri + sizesSuffix + name);
		return JsonUtils.toObject(maybeSize.orElse(""), LabwareSize.class);
	}

	@Override
	public LabwareType findOneType(String name) {
		final Optional<String> maybeType = UrlUtils.parse(uri + typesSuffix + name);
		return JsonUtils.toObject(maybeType.orElse(""), LabwareType.class);	}
}
