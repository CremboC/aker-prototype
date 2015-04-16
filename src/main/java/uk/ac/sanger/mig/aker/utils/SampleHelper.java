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

	/**
	 * Generate barcode from an id
	 *
	 * @param id   id
	 * @param size padding
	 * @return barcode
	 */
	public static String barcodeFromId(long id, int size) {
		return "WTSI" + StringUtils.leftPad(String.valueOf(id), size, PADDING_CHAR);
	}

	/**
	 * Extract id from barcode
	 *
	 * @param barcode barcode
	 * @return id
	 */
	public static Long idFromBarcode(String barcode) {
		return Long.valueOf(StringUtils.removePattern(barcode, "WTSI0+"));
	}

	/**
	 * Extract ids from barcodes
	 *
	 * @param barcodes barcodes
	 * @return list of ids
	 */
	public static Collection<Long> idFromBarcode(Collection<String> barcodes) {
		return barcodes.stream().map(SampleHelper::idFromBarcode).collect(Collectors.toList());
	}

}
