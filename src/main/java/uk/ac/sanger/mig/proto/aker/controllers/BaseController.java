package uk.ac.sanger.mig.proto.aker.controllers;

import org.springframework.stereotype.Component;

/**
 * @author pi1
 * @since February 2015
 */
@Component
public class BaseController {

	private String templatePath;

	/**
	 * Get the view 'link' depending on the action
	 * @param action INDEX, CREATE, EDIT, etc.
	 * @return e.g. 'samples/index'
	 */
	protected String view(Action action) {
		if (templatePath == null) {
			throw new IllegalStateException("Template path must be set");
		}
		return templatePath + "/" + action.toString().toLowerCase();
	}

	public void setTemplatePath(String templatePath) {
		this.templatePath = templatePath;
	}

	public enum Action {
		CREATE,
		EDIT,
		UPDATE,
		DELETE,
		INDEX,
		SHOW,
		STORE
	}

}
