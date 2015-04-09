package uk.ac.sanger.mig.aker.domain;

/**
 * Represents a searchable element
 *
 * @author pi1
 * @since March 2015
 * @param <I>
 */
public interface Searchable<I> {
	/**
	 * The main identifier of the searchable object, e.g. its ID
	 */
	I getIdentifier();

	/**
	 * Path to the searchable, e.g. /samples/show/
	 */
	String getPath();

	/**
	 * What to display in the search results
	 */
	String getSearchName();
}

