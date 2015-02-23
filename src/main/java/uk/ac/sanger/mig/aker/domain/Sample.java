package uk.ac.sanger.mig.aker.domain;

import java.io.Serializable;
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
import javax.persistence.Transient;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

/**
 * @author pi1
 * @since February 2015
 */
@Entity
@Table(name = "samples")
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class)
public class Sample implements Serializable {

	public final static int BARCODE_SIZE = 10;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@ManyToOne(optional = false)
	private Type type;

	@ManyToOne(optional = false)
	private Status status;

	@Column(nullable = false, unique = true)
	private String barcode;

	@OneToMany(mappedBy = "sample", cascade = CascadeType.ALL)
	private Set<Label> labels;

	@ManyToMany(mappedBy = "samples")
	private Set<Group> groups;

	@Transient
	private Label mainLabel;

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

	public Set<Group> getGroups() {
		return groups;
	}

	public void setGroups(Set<Group> groups) {
		this.groups = groups;
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
		if (!barcode.equals(sample.barcode))
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = (int) (id ^ (id >>> 32));
		result = 31 * result + barcode.hashCode();
		return result;
	}

	public Label getMainLabel() {
		return mainLabel;
	}

	public void setMainLabel(Label mainLabel) {
		this.mainLabel = mainLabel;
	}
}
