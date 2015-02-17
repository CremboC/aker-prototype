package uk.ac.sanger.mig.proto.aker.entities;

import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

/**
 * @author pi1
 * @since February 2015
 */
@Entity
public class Sample {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@ManyToOne(optional = false)
	private Type type;

	@Column(nullable = false, unique = true)
	private String barcode;

	@ManyToMany(fetch = FetchType.EAGER)
	private Collection<Label> label;

	public long getId() {
		return id;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public String getBarcode() {
		return barcode;
	}

	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}

	public Collection<Label> getLabel() {
		return label;
	}

	public void setLabel(Collection<Label> label) {
		this.label = label;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		Sample sample = (Sample) o;

		if (id != sample.id)
			return false;
		if (barcode != null ? !barcode.equals(sample.barcode) : sample.barcode != null)
			return false;
		if (type != null ? !type.equals(sample.type) : sample.type != null)
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = (int) (id ^ (id >>> 32));
		result = 31 * result + (barcode != null ? barcode.hashCode() : 0);
		result = 31 * result + (type != null ? type.hashCode() : 0);
		return result;
	}
}
