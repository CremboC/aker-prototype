package uk.ac.sanger.mig.aker.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.ArrayType;
import com.fasterxml.jackson.databind.type.MapType;

/**
 * @author pi1
 * @since March 2015
 */
public class JsonUtils {

	/**
	 * Convert json string to Map string -> string
	 *
	 * @param json to convert
	 * @return map
	 */
	public static Map<String, Object> toMap(String json) {
		ObjectMapper mapper = new ObjectMapper();

		final MapType mapType = mapper.getTypeFactory().constructMapType(Map.class, String.class, Object.class);

		try {
			return (HashMap<String, Object>) mapper.readValue(json, mapType);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return new HashMap<>();
	}

	public static List<Map<String, String>> toListOfMaps(String json) {
		ObjectMapper mapper = new ObjectMapper();

		final ArrayType arrayType = mapper.getTypeFactory()
				.constructArrayType(List.class);

		try {
			return (ArrayList<Map<String, String>>) mapper.readValue(json, arrayType);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return new ArrayList<>();
	}

}
