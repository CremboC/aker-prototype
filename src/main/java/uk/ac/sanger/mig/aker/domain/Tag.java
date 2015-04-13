package uk.ac.sanger.mig.aker.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

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

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (obj.getClass() != getClass()) {
			return false;
		}
		Tag rhs = (Tag) obj;
		return new EqualsBuilder()
				.append(this.name, rhs.name)
				.append(this.value, rhs.value)
				.append(this.sample, rhs.sample)
				.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
				.append(name)
				.append(value)
				.append(sample)
				.toHashCode();
	}
}
