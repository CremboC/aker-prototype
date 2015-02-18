package uk.ac.sanger.mig.proto.aker.controllers;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author pi1
 * @since February 2015
 */
@Controller
public class IndexController extends BaseController {

	@PostConstruct
	private void init() {
		setTemplatePath("main");
	}

	@RequestMapping("/")
	public String greeting(@RequestParam(value = "name", required = false, defaultValue = "World") String name,
			Model model) {
		model.addAttribute("name", name);

		return view(Action.INDEX);
	}

}
