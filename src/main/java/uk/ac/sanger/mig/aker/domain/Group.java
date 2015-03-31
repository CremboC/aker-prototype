package uk.ac.sanger.mig.aker.domain;

import java.util.Collection;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

/**
 * @author pi1
 * @since February 2015
 */
@Entity
@Table(name = "groups")
public class Group extends BaseEntity {

	@Column
	private String name;

	@Column(nullable = false)
	private String owner;

	@ManyToMany
	@JoinTable(
			name = "groups_samples",
			joinColumns = @JoinColumn(name = "group_id", referencedColumnName = "id"),
			inverseJoinColumns = @JoinColumn(name = "sample_id", referencedColumnName = "id")
	)
	@JsonManagedReference
	private Set<Sample> samples;

	@OneToOne
	@JoinColumn(name = "parent_id")
	private Group parent;

	@Transient
	@JsonIgnore
	private Collection<Group> children;

	@ManyToOne
	@JoinColumn(name = "type_id")
	private Type type;

	@Transient
	private boolean remove;

	public Set<Sample> getSamples() {
		return samples;
	}

	public void setSamples(Set<Sample> samples) {
		this.samples = samples;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Group getParent() {
		return parent;
	}

	public void setParent(Group parent) {
		this.parent = parent;
	}

	public Collection<Group> getChildren() {
		return children;
	}

	public void setChildren(Collection<Group> children) {
		this.children = children;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public String getOwner() {
		return owner;
	}

	public boolean isRemove() {
		return remove;
	}

	public void setRemove(boolean remove) {
		this.remove = remove;
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
		Group rhs = (Group) obj;
		return new EqualsBuilder()
				.appendSuper(super.equals(obj))
				.append(this.name, rhs.name)
				.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
				.appendSuper(super.hashCode())
				.append(name)
				.toHashCode();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this)
				.appendSuper(super.toString())
				.append("name", name)
				.append("owner", owner)
				.append("parent", parent)
				.append("children", children)
				.toString();
	}
}
