package uk.ac.sanger.mig.aker.controllers;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import uk.ac.sanger.mig.aker.domain.external.LabwareSize;
import uk.ac.sanger.mig.aker.domain.requests.LabwareRequest;
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

	@Resource(name = "labwareRequestValidator")
	private Validator validator;

	@InitBinder
	protected void initBinder(WebDataBinder binder) {
		binder.setValidator(validator);
	}

	@PostConstruct
	private void init() {
		setTemplatePath("labware");
	}

	@RequestMapping(value = "/create", method = RequestMethod.POST)
	public String create(@Valid @ModelAttribute LabwareRequest labwareRequest, Errors errors, Model model) {
		if (errors.hasErrors()) {
			return "redirect:/";
		}

		final LabwareSize size = labwareService.findOneSize(labwareRequest.getSize());
		final int totalSize = size.getColumns() * size.getRows();

		Collection<String> emptySamples = new ArrayList<>(totalSize);
		emptySamples.addAll(labwareRequest.getSamples());

		labwareRequest.setSamples(emptySamples);

		model.addAttribute("labware", labwareRequest);
		model.addAttribute("size", size);

		return view(Action.CREATE);
	}

	@RequestMapping(value = "/get", method = RequestMethod.GET)
	@ResponseBody
	public Object queryLabware(Principal principal) {
		return labwareService.findAll(principal.getName()).orElse("");
	}

	@RequestMapping(value = "/get/types", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> queryTypes() {
		return labwareService.findAllTypes();
	}

	@RequestMapping(value = "/get/sizes", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> querySizes() {
		return labwareService.findAllSizes();
	}

}
