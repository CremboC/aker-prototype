package uk.ac.sanger.mig.aker.controllers;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.StreamSupport;

import javax.annotation.PostConstruct;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.util.StringUtils;

import uk.ac.sanger.mig.aker.domain.Group;
import uk.ac.sanger.mig.aker.domain.GroupRequest;
import uk.ac.sanger.mig.aker.domain.Sample;
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
		model.addAttribute("groupRequest", new GroupRequest());

		return view(Action.INDEX);
	}

	@RequestMapping(value = "/json", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public Page<Group> json(Pageable pageable) {
		final Page<Group> all = groupService.findAll(pageable);

		// remove parent's parent to simlify things for convertion into json..
		StreamSupport.stream(all.spliterator(), false) // get stream
				.filter(g -> g.getParent() != null) // filter groups with no parent
				.forEach(g -> g.getParent().setParent(null)); // set parent's parent to null

		return all;
	}

	@RequestMapping(value = "/show/{id}", method = RequestMethod.GET)
	public String show(@PathVariable Long id, Model model) {
		final Group group = groupRepository.findOne(id);

		final Iterable<Group> allGroups = groupRepository.findAllByIdNotIn(id);

		final Set<Group> byParentId = groupRepository.findByParentId(group.getId());
		if (!byParentId.isEmpty()) {
			group.setChildren(byParentId);
		}

		model.addAttribute("group", group);
		model.addAttribute("subgroup", new Group());
		model.addAttribute("groups", allGroups);
		return view(Action.SHOW);
	}

	@RequestMapping(value = "/update/{id}/add-subgroup", method = RequestMethod.PUT)
	public String addSubgroup(@PathVariable long id, @ModelAttribute Group addGroup, BindingResult result) {
		if (addGroup.getId() == id) {
			return "redirect:/groups/show/" + id;
		}

		Group group = groupRepository.findOne(id);
		Group subgroup = groupRepository.findOne(addGroup.getId());

		if (!result.hasErrors()) {
			subgroup.setParent(group);
			groupRepository.save(subgroup);
		}

		return "redirect:/groups/show/" + group.getId();
	}

	@RequestMapping(value = "/group", method = RequestMethod.POST)
	public String group(@ModelAttribute GroupRequest groupRequest, BindingResult binding) {
		if (!binding.hasErrors()) {
			groupService.createGroup(groupRequest);

			return "redirect:/";
		}

		return view("group");
	}

	@RequestMapping(value = "/byType", method = RequestMethod.GET)
	@ResponseBody
	public Page<Sample> byType(@RequestParam("types") String types, Pageable pageable) {
		Set<String> typeSet = new HashSet<>();
		CollectionUtils.addAll(typeSet, StringUtils.split(types, ","));
		System.out.println(typeSet);
		return groupRepository.findAllByTypeIn(typeSet, pageable);
	}

}
