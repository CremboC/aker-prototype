package uk.ac.sanger.mig.proto.aker.entities;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.apache.commons.lang3.StringUtils;

/**
 * @author pi1
 * @since February 2015
 */
@Entity
@Table(name = "samples")
public class Sample {

	public final static int BARCODE_SIZE = 10;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@ManyToOne(optional = false)
	private Type type;

	@ManyToOne(optional = false)
	private Status status;

	@Column
	private String name;

	@Column(nullable = false, unique = true)
	private String barcode;

	@OneToMany(mappedBy = "sample", cascade = CascadeType.ALL)
	private Set<Label> labels;

	@ManyToMany(mappedBy = "samples")
	private Set<Group> groups;

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

	public String createBarcode(long lastId) {
		return "WTSI" + StringUtils.leftPad(String.valueOf(lastId), BARCODE_SIZE, '0');
	}

	public Set<Label> getLabels() {
		return labels;
	}

	public void setLabels(Set<Label> labels) {
		this.labels = labels;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Set<Group> getGroups() {
		return groups;
	}

	public void setGroups(Set<Group> groups) {
		this.groups = groups;
	}
}
