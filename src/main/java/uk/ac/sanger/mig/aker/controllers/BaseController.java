package uk.ac.sanger.mig.aker.controllers;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

/**
 * @author pi1
 * @since February 2015
 */
public abstract class BaseController {

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

	/**
	 * Shortcut to a redirect. If "location" starts with a /, it is an absolute redirect, otherwise it's local.
	 * E.g. <br>
	 * <p>
	 * <code>redirect("/samples/")</code> will result in <code>redirect:/samples/</code>
	 * <p>
	 * <code>redirect("create")</code>, inside a "groups" controller will result in <code>redirect:/groups/create</code>
	 *
	 * @param location location to redirect to
	 * @return correctly formatted redirection string
	 */
	protected String redirect(String location) {
		Assert.notNull(templatePath);

		return "redirect:" + (!location.startsWith("/") ? "/" + templatePath + "/" + location : location);
	}

	protected String redirect(String location, Object... params) {
		final String r = redirect(location);

		return StringUtils.appendIfMissing(r, "/") + StringUtils.join(params, '/');
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
