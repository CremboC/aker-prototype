package uk.ac.sanger.mig.aker.controllers;

/**
 * @author pi1
 * @since February 2015
 */
public class BaseController {

	private String templatePath;

	/**
	 * Get the view 'link' depending on the action
	 *
	 * @param action INDEX, CREATE, EDIT, etc.
	 * @return e.g. 'samples/index'
	 */
	protected String view(Action action) {
		return view(action.toString().toLowerCase());
	}

	/**
	 * Get the view 'link' with the given view name
	 *
	 * @param view name of view file, e.g. 'request'
	 * @return e.g. 'samples/request'
	 */
	protected String view(String view) {
		if (templatePath == null) {
			throw new IllegalStateException("Template path must be set");
		}
		return templatePath + "/" + view;
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
