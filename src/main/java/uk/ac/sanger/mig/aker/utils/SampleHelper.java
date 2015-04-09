package uk.ac.sanger.mig.aker.utils;

import java.util.Collection;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

/**
 * @author pi1
 * @since April 2015
 */
public class SampleHelper {

	public final static char PADDING_CHAR = '0';

	private SampleHelper() {
	}

	public static String getBarcode(long id, int size) {
		return "WTSI" + StringUtils.leftPad(String.valueOf(id), size, PADDING_CHAR);
	}

	public static Long idFromBarcode(String barcode) {
		return Long.valueOf(StringUtils.removePattern(barcode, "WTSI0+"));
	}

	public static Collection<Long> idFromBarcode(Collection<String> barcode) {
		return barcode.stream().map(SampleHelper::idFromBarcode).collect(Collectors.toList());
	}

}
