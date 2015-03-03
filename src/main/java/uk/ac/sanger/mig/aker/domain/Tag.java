package uk.ac.sanger.mig.aker.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author pi1
 * @since March 2015
 */
@Entity
@Table(name = "tags")
public class Tag extends BaseEntity {

	@Column(nullable = false)
	@NotNull
	private String name;

	@Column(nullable = false)
	@NotNull
	private String value;

	@ManyToOne(optional = false)
	@JoinColumn(name = "sample_id")
	@JsonIgnore
	private Sample sample;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Sample getSample() {
		return sample;
	}

	public void setSample(Sample sample) {
		this.sample = sample;
	}
}
