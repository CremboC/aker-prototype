package uk.ac.sanger.mig.aker.controllers;

import java.security.Principal;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import uk.ac.sanger.mig.aker.requests.LabwareRequest;
import uk.ac.sanger.mig.aker.services.LabwareService;

/**
 * @author pi1
 * @since March 2015
 */
@Controller
@RequestMapping("/labware")
public class LabwareController extends BaseController {

	@Resource
	private LabwareService labwareService;

	@PostConstruct
	private void init() {
		setTemplatePath("labware");
	}

	@RequestMapping(value = "/create", method = RequestMethod.POST)
	private String create(@Valid @ModelAttribute LabwareRequest labwareRequest, Errors errors, Model model) {
		if (errors.hasErrors()) {
			return "redirect:/";
		}

		model.addAttribute("labware", labwareRequest);

		return view(Action.CREATE);
	}

	@RequestMapping(value = "/get", method = RequestMethod.GET)
	@ResponseBody
	private Object queryLabware(Principal principal) {
		return labwareService.queryAll(principal.getName()).orElse("");
	}

	@RequestMapping(value = "/get/types", method = RequestMethod.GET)
	@ResponseBody
	private Map<String, Object> queryTypes() {
		return labwareService.queryTypes();
	}

	@RequestMapping(value = "/get/sizes", method = RequestMethod.GET)
	@ResponseBody
	private Map<String, Object> querySizes() {
		return labwareService.querySizes();
	}

}
