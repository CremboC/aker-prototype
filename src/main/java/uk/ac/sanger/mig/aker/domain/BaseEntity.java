package uk.ac.sanger.mig.aker.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.data.annotation.CreatedDate;

/**
 * @author pi1
 * @since February 2015
 */
@MappedSuperclass
public abstract class BaseEntity {

	@Id
	@GeneratedValue
	protected Long id;

	@CreatedDate
	@Column(name = "create_date")
	protected Date created;

	@Version
	@Column(name = "update_date")
	protected Date update;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public Date getUpdate() {
		return update;
	}

	public void setUpdate(Date update) {
		this.update = update;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this)
				.append("id", id)
				.append("created", created)
				.append("update", update)
				.toString();
	}
}
