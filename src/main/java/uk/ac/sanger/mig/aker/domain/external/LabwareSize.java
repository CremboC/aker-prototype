package uk.ac.sanger.mig.aker.domain.external;

/**
 * @author pi1
 * @since March 2015
 */
public class LabwareSize extends ExternalMappable {

	private String name;
	private Integer columns;
	private Integer rows;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getRows() {
		return rows;
	}

	public void setRows(Integer rows) {
		this.rows = rows;
	}

	public Integer getColumns() {
		return columns;
	}

	public void setColumns(Integer columns) {
		this.columns = columns;
	}
}
