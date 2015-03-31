package uk.ac.sanger.mig.aker.domain;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

/**
 * @author pi1
 * @since February 2015
 */
@Entity
@Table(name = "samples")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Sample extends BaseEntity implements Serializable, Searchable<String> {

	public final static int BARCODE_SIZE = 10;

	@ManyToOne(optional = false, cascade = CascadeType.ALL)
	private Type type;

	@ManyToOne(optional = false, cascade = CascadeType.ALL)
	private Status status;

	@Column(nullable = false, unique = true)
	private String barcode;

	@OneToMany(mappedBy = "sample", cascade = CascadeType.ALL)
	private Set<Alias> aliases;

	@ManyToMany(mappedBy = "samples", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JsonBackReference
	private Set<Group> groups;

	@OneToMany(mappedBy = "sample", fetch = FetchType.EAGER)
	private Set<Tag> tags;

	@Column(nullable = false)
	private String owner;

	@Transient
	private Alias mainAlias = null;

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

	public Set<Alias> getAliases() {
		return aliases;
	}

	public void setAliases(Set<Alias> aliases) {
		this.aliases = aliases;
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

	public Alias getMainAlias() {
		return mainAlias;
	}

	public void setMainAlias(Alias mainAlias) {
		this.mainAlias = mainAlias;
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
		Sample rhs = (Sample) obj;
		return new EqualsBuilder()
				.appendSuper(super.equals(obj))
				.append(this.barcode, rhs.barcode)
				.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
				.appendSuper(super.hashCode())
				.append(barcode)
				.toHashCode();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this)
				.appendSuper(super.toString())
				.append("type", type)
				.append("status", status)
				.append("barcode", barcode)
				.append("aliases", aliases)
				.append("groups", groups)
				.append("mainAlias", mainAlias)
				.toString();
	}

	public Set<Tag> getTags() {
		return tags;
	}

	public void setTags(Set<Tag> tags) {
		this.tags = tags;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	@Override
	public String getIdentifier() {
		return barcode;
	}

	@Override
	public String getPath() {
		return "/samples/show/";
	}

	@Override
	@JsonIgnore
	public String getSearchResult() {
		return mainAlias.getName();
	}
}
