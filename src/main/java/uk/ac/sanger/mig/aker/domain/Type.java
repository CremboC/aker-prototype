package uk.ac.sanger.mig.aker.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * @author pi1
 * @since February 2015
 */
@Entity
@Table(name = "types")
public class Type {

	@Id
	@GeneratedValue
	private long id;

	@Column(unique = true, nullable = false)
	private String value;

	public long getId() {
		return id;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
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
		Type rhs = (Type) obj;
		return new EqualsBuilder()
				.append(this.value, rhs.value)
				.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
				.append(value)
				.toHashCode();
	}
}
