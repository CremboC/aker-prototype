package uk.ac.sanger.mig.aker.domain.requests;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.stream.Collectors.toMap;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import uk.ac.sanger.mig.aker.domain.Sample;
import uk.ac.sanger.mig.aker.domain.Tag;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author pi1
 * @since March 2015
 */
@Component
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class OrderRequest {

	private Collection<OrderSample> samples = new ArrayList<>();
	private List<Long> groups = new ArrayList<>();
	private OrderProduct product = new OrderProduct();
	private OrderProject project = new OrderProject();
	private Collection<OrderOption> options = new ArrayList<>();

	@JsonIgnore
	private boolean processed = false;

	private Double estimateCost = 0.0d;

	public Collection<OrderSample> getSamples() {
		return samples;
	}

	public void setSamples(Collection<OrderSample> samples) {
		this.samples = new LinkedHashSet<>(samples);
	}

	public Collection<OrderOption> getOptions() {
		return options;
	}

	public void setOptions(Collection<OrderOption> options) {
		this.options = options;
	}

	public OrderProduct getProduct() {
		return product;
	}

	public void setProduct(OrderProduct product) {
		this.product = product;
	}

	public boolean isProcessed() {
		return processed;
	}

	public void setProcessed(boolean processed) {
		this.processed = processed;
	}

	public List<Long> getGroups() {
		return groups;
	}

	public void setGroups(List<Long> groups) {
		this.groups = groups;
	}

	public Double getEstimateCost() {
		return estimateCost;
	}

	public void setEstimateCost(Double estimateCost) {
		this.estimateCost = estimateCost;
	}

	public OrderProject getProject() {
		return project;
	}

	public void setProject(OrderProject project) {
		this.project = project;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this)
				.append("samples", samples)
				.append("product", product)
				.append("options", options)
				.append("processed", processed)
				.toString();
	}

	public static class OrderSample {
		private String barcode;
		private Map<String, String> options = new HashMap<>();

		public OrderSample() {
		}

		public OrderSample(Sample sample) {
			barcode = sample.getBarcode();
			options = sample.getTags().stream().collect(toMap(Tag::getName, Tag::getValue));
		}

		public String getBarcode() {
			return barcode;
		}

		public void setBarcode(String barcode) {
			this.barcode = barcode;
		}

		public Map<String, String> getOptions() {
			return options;
		}

		public void setOptions(Map<String, String> options) {
			this.options = options;
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
			OrderSample rhs = (OrderSample) obj;
			return new EqualsBuilder()
					.append(this.barcode, rhs.barcode)
					.isEquals();
		}

		@Override
		public int hashCode() {
			return new HashCodeBuilder()
					.append(barcode)
					.toHashCode();
		}

		@Override
		public String toString() {
			return new ToStringBuilder(this)
					.append("barcode", barcode)
					.append("options", options)
					.toString();
		}
	}

	public static class OrderOption {
		private String name;
		private String value;

		private Collection<String> restrictedOptions = new ArrayList<>();

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public Collection<String> getRestrictedOptions() {
			return restrictedOptions;
		}

		public void setRestrictedOptions(Collection<String> restrictedOptions) {
			this.restrictedOptions = restrictedOptions;
		}

		@Override
		public String toString() {
			return new ToStringBuilder(this)
					.append("name", name)
					.append("value", value)
					.append("restrictedOptions", restrictedOptions)
					.toString();
		}
	}

	public static class OrderProduct {
		private String name;
		private Set<OrderOption> options = new LinkedHashSet<>();
		private Double unitCost;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public Set<OrderOption> getOptions() {
			return options;
		}

		public void setOptions(Set<OrderOption> options) {
			this.options = options;
		}

		public Double getUnitCost() {
			return unitCost;
		}

		public void setUnitCost(Double unitCost) {
			this.unitCost = unitCost;
		}

		@Override
		public String toString() {
			return new ToStringBuilder(this)
					.append("name", name)
					.append("options", options)
					.toString();
		}
	}

	public static class OrderProject {

		private String code;

		public String getCode() {
			return code;
		}

		public void setCode(String code) {
			this.code = code;
		}
	}

}
