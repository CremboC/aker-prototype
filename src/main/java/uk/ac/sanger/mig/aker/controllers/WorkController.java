package uk.ac.sanger.mig.aker.controllers;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author pi1
 * @since February 2015
 */
@Controller
@RequestMapping("/work/")
public class WorkController extends BaseController {

	@PostConstruct
	private void init() {
		setTemplatePath("work");
	}

	@RequestMapping("/")
	public String index() {
		return view(Action.INDEX);
	}

}
