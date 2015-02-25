package uk.ac.sanger.mig.aker.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
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
	protected long id;

	@CreatedDate
	@Column(name = "create_date")
	protected Date created;

	@Version
	@Column(name = "update_date")
	protected Date update;

	public long getId() {
		return id;
	}

	public void setId(long id) {
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
		BaseEntity rhs = (BaseEntity) obj;
		return new EqualsBuilder()
				.append(this.id, rhs.id)
				.append(this.created, rhs.created)
				.append(this.update, rhs.update)
				.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
				.append(id)
				.append(created)
				.append(update)
				.toHashCode();
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
