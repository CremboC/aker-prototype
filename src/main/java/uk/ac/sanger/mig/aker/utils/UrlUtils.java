package uk.ac.sanger.mig.aker.utils;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.Scanner;

/**
 * @author pi1
 * @since March 2015
 */
public class UrlUtils {

	/**
	 * Given a string url/path, opens it, reads it and returns it as a string.
	 * <p>
	 * Can be used to read RESTful API's and proxy them – pass the url to a resource that returns JSON and this
	 * method will return the JSON as a string – this can later be passed to a controller
	 *
	 * @param path path to resource
	 * @return resource result
	 */
	public static Optional<String> parse(String path) {
		try {
			final URL url = new URL(path);
			try (final Scanner s = new Scanner(url.openStream())) {
				return Optional.of(s.useDelimiter("\\A").hasNext() ? s.next() : "");
			}
		} catch (IOException e) {
			e.printStackTrace();
			return Optional.empty();
		}
	}
}


