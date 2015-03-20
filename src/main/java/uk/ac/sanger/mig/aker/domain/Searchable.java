package uk.ac.sanger.mig.aker.domain;

/**
 * @author pi1
 * @since March 2015
 * @param <I>
 */
public interface Searchable<I> {
	I getIdentifier();
	String getPath();
	String getSearchResult();
}

