package uk.ac.sanger.mig.aker.controllers;

import java.util.Collection;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import uk.ac.sanger.mig.aker.domain.Searchable;
import uk.ac.sanger.mig.aker.services.SampleService;

/**
 * @author pi1
 * @since March 2015
 */
@Controller
@RequestMapping("/search")
public class SearchController extends BaseController {

	@Resource
	private SampleService sampleService;

	@PostConstruct
	private void init() {
		setTemplatePath("search");
	}

	@RequestMapping("/samples")
	private String samples(@RequestParam("search") String query, Model model) {
		Collection<Searchable<?>> samples = sampleService.search(query);

		model.addAttribute("results", samples);

		return view(Action.INDEX);
	}

}
