package uk.ac.sanger.mig.aker.controllers;

import java.security.Principal;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import uk.ac.sanger.mig.aker.domain.external.LabwareSize;
import uk.ac.sanger.mig.aker.domain.requests.LabwareRequest;
import uk.ac.sanger.mig.aker.services.LabwareService;

/**
 * @author pi1
 * @since March 2015
 */
@Controller
@RequestMapping("/labware")
public class LabwareController {

	@Autowired
	private LabwareService labwareService;

	@Resource(name = "labwareRequestValidator")
	private Validator validator;

	@InitBinder("labwareRequest")
	protected void initBinder(WebDataBinder binder) {
		binder.setValidator(validator);
	}

	@RequestMapping(value = "/create", method = RequestMethod.POST)
	public ModelAndView create(
			@Valid @ModelAttribute("labwareRequest") LabwareRequest labwareRequest,
			Errors errors,
			HttpSession session) {

		final ModelAndView mav = new ModelAndView();

		if (errors.hasErrors()) {
			mav.setViewName("redirect:/");
			return mav;
		}

		session.setAttribute("labware", labwareRequest);

		mav.setViewName("redirect:/labware/create/");

		return mav;
	}

	@RequestMapping(value = "/create", method = RequestMethod.GET)
	public ModelAndView submit(HttpSession session) {
		final ModelAndView mav = new ModelAndView();
		final LabwareRequest labware = (LabwareRequest) session.getAttribute("labware");

		if (labware == null) {
			mav.setViewName("redirect:/");
			return mav;
		}

		final LabwareSize size = labwareService.findOneSize(labware.getSize());

		mav.addObject("size", size);
		mav.addObject("labwareRequest", labware);

		mav.setViewName("labware/create");
		return mav;
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
