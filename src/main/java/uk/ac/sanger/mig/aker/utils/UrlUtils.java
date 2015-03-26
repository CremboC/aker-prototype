package uk.ac.sanger.mig.aker.utils;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author pi1
 * @since March 2015
 */
public class UrlUtils {

	private final static Logger logger = LoggerFactory.getLogger(UrlUtils.class);

	private UrlUtils() {
	}


	/**
	 * Given a string url/path, opens it, reads it and returns it as a string.
	 * <p>
	 * Can be used to read RESTful API's and proxy them – pass the url to a resource that returns JSON and this
	 * method will return the JSON as a string – this can later be passed to a controller
	 *
	 * @param path path to resource
	 * @return resource result, <b>empty</b> upon failure to parse URL (IOException)
	 */
	public static Optional<String> parse(String path) {
		try {
			final URL url = new URL(path);
			try (final Scanner s = new Scanner(url.openStream())) {
				return Optional.of(readWholeString(s));
			}
		} catch (IOException e) {
			logger.error("Attempted to parse " + path);
			logger.error(e.getMessage());
			return Optional.empty();
		}
	}

	private static String readWholeString(Scanner s) {
		return s.useDelimiter("\\A").hasNext() ? s.next() : "";
	}
}


