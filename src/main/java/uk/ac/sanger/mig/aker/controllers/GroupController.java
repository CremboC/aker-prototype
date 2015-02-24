package uk.ac.sanger.mig.aker.controllers;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import uk.ac.sanger.mig.aker.domain.Group;
import uk.ac.sanger.mig.aker.repositories.GroupRepository;
import uk.ac.sanger.mig.aker.repositories.SampleRepository;
import uk.ac.sanger.mig.aker.services.GroupService;

/**
 * @author pi1
 * @since February 2015
 */
@Controller
@RequestMapping("/groups")
public class GroupController extends BaseController {

	@Autowired
	private GroupService groupService;

	@Autowired
	private GroupRepository groupRepository;

	@Autowired
	private SampleRepository sampleRepository;

	@PostConstruct
	private void init() {
		setTemplatePath("groups");
	}

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String index(Model model) {
		return view(Action.INDEX);
	}

	@RequestMapping(value = "/json", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public Page<Group> json(Pageable pageable) {
		final Page<Group> all = groupService.findAll(pageable);

		return all;
	}

	@RequestMapping(value = "/show/{id}")
	public String show(@PathVariable long id, Model model) {
		model.addAttribute("group", groupRepository.findOne(id));
		return view(Action.SHOW);
	}

}
