package uk.ac.sanger.mig.aker.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.util.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.MapType;

/**
 * @author pi1
 * @since March 2015
 */
public class JsonUtils {

	private final static Logger logger = LoggerFactory.getLogger(JsonUtils.class);

	private JsonUtils() {
	}

	/**
	 * Convert json string to Map string -> object
	 *
	 * @param json to convert
	 * @return map
	 */
	public static Map<String, Object> toMap(String json) {
		ObjectMapper mapper = new ObjectMapper();

		final MapType mapType = mapper.getTypeFactory().constructMapType(Map.class, String.class, Object.class);

		if (json.equals("")) {
			return new HashMap<>();
		}

		try {
			return (HashMap<String, Object>) mapper.readValue(json, mapType);
		} catch (IOException e) {
			logFailedParsing(json, e);
		}

		return new HashMap<>();
	}

	/**
	 * Maps to a generic collection of maps
	 *
	 * @param json to convert
	 * @return collection of maps
	 */
	public static Collection<Object> toCollection(String json) {
		ObjectMapper mapper = new ObjectMapper();

		final CollectionType arrayType = mapper.getTypeFactory().constructCollectionType(List.class, Map.class);

		try {
			return (Collection<Object>) mapper.readValue(json, arrayType);
		} catch (IOException e) {
			logFailedParsing(json, e);
		}

		return new ArrayList<>();
	}

	/**
	 * Maps JSON to the specified class
	 *
	 * @param json json to parse
	 * @param clazz class to map to
	 * @param <T> any class
	 * @return T upon success, <b>null</b> otherwise
	 */
	public static <T> T toObject(String json, Class<T> clazz) {
		ObjectMapper mapper = new ObjectMapper();

		try {
			return mapper.readValue(json, clazz);
		} catch (IOException e) {
			logFailedParsing(json, e);
		}

		return null;
	}

	/**
	 * Maps JSON to a collection of objects
	 *
	 * @param json json to parse
	 * @param clazz class to map to
	 * @param <T> any class
	 * @return Collection of T upon success, <b>null</b> otherwise
	 */
	public static <T> Collection<T> toObjects(String json, Class<T> clazz) {
		ObjectMapper mapper = new ObjectMapper();

		final CollectionType collectionType = mapper.getTypeFactory().constructCollectionType(List.class, clazz);

		try {
			return mapper.readValue(json, collectionType);
		} catch (IOException e) {
			logFailedParsing(json, e);
		}

		return null;
	}

	private static void logFailedParsing(String json, Exception e) {
		logger.error(e.getMessage() + " when mapping " + json);
		logger.error(StringUtils.concat(e.getStackTrace()));
	}
}
