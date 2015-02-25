package uk.ac.sanger.mig.aker.domain;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author pi1
 * @since February 2015
 */
@Entity
@Table(name = "labels", indexes = {
		@Index(columnList = "id, main", unique = true)
})
public class Label {

	@Id
	@GeneratedValue
	private long id;

	@Column(unique = false, nullable = false)
	private String name;

	@Column(nullable = false)
	private boolean main = false;

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "sample_id")
	@JsonIgnore
	private Sample sample;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Sample getSample() {
		return sample;
	}

	public void setSample(Sample sample) {
		this.sample = sample;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		Label label = (Label) o;

		if (id != label.id)
			return false;
		if (!name.equals(label.name))
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = (int) (id ^ (id >>> 32));
		result = 31 * result + name.hashCode();
		return result;
	}

	public boolean isMain() {
		return main;
	}

	public void setMain(boolean main) {
		this.main = main;
	}
}
