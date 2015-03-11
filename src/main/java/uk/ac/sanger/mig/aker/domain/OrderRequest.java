package uk.ac.sanger.mig.aker.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author pi1
 * @since March 2015
 */
@Component
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class OrderRequest {

	private List<OrderSample> samples = new ArrayList<>();
	private List<Long> groups = new ArrayList<>();
	private OrderProduct product = new OrderProduct();
	private OrderProject project = new OrderProject();
	private Set<OrderOption> options = new HashSet<>();

	@JsonIgnore
	private boolean processed = false;

	private Double estimateCost;

	public List<OrderSample> getSamples() {
		return samples;
	}

	public void setSamples(List<OrderSample> samples) {
		this.samples = samples;
	}

	public Set<OrderOption> getOptions() {
		return options;
	}

	public void setOptions(Set<OrderOption> options) {
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

		private Set<String> restrictedOptions = new HashSet<>();

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

		public Set<String> getRestrictedOptions() {
			return restrictedOptions;
		}

		public void setRestrictedOptions(Set<String> restrictedOptions) {
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
