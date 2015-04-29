package uk.ac.sanger.mig.aker.domain.requests;

/**
 * @author pi1
 * @since April 2015
 */
public class Response {

	private Status status;
	private String message;

	public Response(Status status, String message) {
		this.status = status;
		this.message = message;
	}

	public Status getStatus() {
		return status;
	}

	public String getMessage() {
		return message;
	}

	public enum Status {
		SUCCESS("Success"),
		FAIL("Error");

		private final String name;

		Status(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}
	}
}
