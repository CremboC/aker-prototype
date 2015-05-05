package uk.ac.sanger.mig.aker.controllers;

import java.security.Principal;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import uk.ac.sanger.mig.aker.domain.Searchable;
import uk.ac.sanger.mig.aker.services.GroupService;
import uk.ac.sanger.mig.aker.services.SampleService;

/**
 * @author pi1
 * @since March 2015
 */
@Controller
@RequestMapping("/search")
public class SearchController {

	@Autowired
	private SampleService sampleService;

	@Autowired
	private GroupService groupService;

	@RequestMapping("/")
	public String index(@RequestParam("search") String query, Model model, Principal principal) {
		Collection<Searchable<?>> groups = groupService.search(query, principal.getName());
		Collection<Searchable<?>> samples = sampleService.search(query, principal.getName());

		Map<String, Collection<Searchable<?>>> results = new HashMap<>();
		results.put("Groups", groups);
		results.put("Samples", samples);

		model.addAttribute("results", results);

		return "search/index";
	}

	@RequestMapping("/samples")
	public String samples(@RequestParam("search") String query, Model model, Principal principal) {
		Collection<Searchable<?>> samples = sampleService.search(query, principal.getName());

		model.addAttribute("results", samples);

		return "search/index";
	}


}
