package uk.ac.sanger.mig.aker.controllers;

import java.security.Principal;
import java.util.Collection;
import java.util.Set;
import java.util.stream.StreamSupport;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;

import uk.ac.sanger.mig.aker.domain.Group;
import uk.ac.sanger.mig.aker.domain.Sample;
import uk.ac.sanger.mig.aker.domain.requests.GroupRequest;
import uk.ac.sanger.mig.aker.repositories.GroupRepository;
import uk.ac.sanger.mig.aker.services.GroupService;
import uk.ac.sanger.mig.aker.services.SampleService;

/**
 * @author pi1
 * @since February 2015
 */
@Controller
@RequestMapping("/groups")
@SessionAttributes({
		"group"
})
public class GroupController extends BaseController {

	@Autowired
	private GroupService groupService;

	@Autowired
	private SampleService sampleService;

	@Resource(name = "groupRequestValidator")
	private Validator groupRequestValidator;

	private GroupRepository groupRepository;

	@InitBinder("groupRequest")
	protected void initGroupRequestBinder(WebDataBinder binder) {
		binder.setValidator(groupRequestValidator);
	}

	@InitBinder("group")
	protected void initGroupBinder(WebDataBinder binder) {
		//		binder.setValidator(groupRequestValidator);
	}

	@PostConstruct
	private void init() {
		setTemplatePath("groups");
		groupRepository = groupService.getRepository();
	}

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String index(Model model) {
		model.addAttribute("groupRequest", new GroupRequest());

		return view(Action.INDEX);
	}

	@RequestMapping(value = "/create", method = RequestMethod.GET)
	public String create(Model model) {
		model.addAttribute("groupRequest", new GroupRequest());

		return view(Action.CREATE);
	}

	@RequestMapping(value = "/store", method = RequestMethod.POST)
	public String store(@Valid @ModelAttribute("groupRequest") GroupRequest groupRequest, Errors errors) {
		if (errors.hasErrors()) {
			return view(Action.CREATE);
		}

		setGroupRequestType(groupRequest);
		groupService.createGroup(groupRequest);

		return "redirect:/groups/";
	}

	@RequestMapping(value = "/show/{id}", method = RequestMethod.GET)
	public String show(@PathVariable Long id, Model model, Principal user) {
		final Group group = groupRepository.findOne(id);

		if (!group.getOwner().equals(user.getName())) {
			return "redirect:/";
		}

		final Collection<Group> allGroups = groupRepository.findAllByIdNotAndOwner(id, user.getName());

		final Set<Group> byParentId = groupRepository.findByParentId(group.getId());
		if (!byParentId.isEmpty()) {
			group.setChildren(byParentId);
		}

		model.addAttribute("group", group);
		model.addAttribute("subgroup", new Group());
		model.addAttribute("groups", allGroups);

		return view(Action.SHOW);
	}

	@RequestMapping(value = "/edit/{id}", method = RequestMethod.GET)
	public String edit(@PathVariable Long id, Model model, Principal user) {
		final Group group = groupRepository.findOne(id);

		if (!group.getOwner().equals(user.getName())) {
			return "redirect:/";
		}

		final Collection<Group> allGroups = groupRepository.findAllByIdNotAndOwner(id, user.getName());

		final Set<Group> byParentId = groupRepository.findByParentId(group.getId());
		if (!byParentId.isEmpty()) {
			group.setChildren(byParentId);
		}

		model.addAttribute("group", group);
		model.addAttribute("subgroup", new Group());
		model.addAttribute("groups", allGroups);

		return view(Action.EDIT);
	}

	@RequestMapping(value = "/update/{id}", method = RequestMethod.PUT)
	public String update(@PathVariable Long id, @Valid @ModelAttribute("group") Group groupUpdate, Errors errors, Principal principal) {
		if (errors.hasErrors()) {
			return view(Action.EDIT);
		}

		final Group group = groupRepository.findOne(id);

		if (group == null || !group.getOwner().equals(principal.getName())) {
			return "redirect:/groups";
		}

		groupService.save(groupUpdate);

		return "redirect:/groups/show/" + id;
	}

	@RequestMapping(value = "/json", method = RequestMethod.GET)
	@ResponseBody
	public Page<Group> json(Pageable pageable, Principal principal) {
		final Page<Group> groups = groupRepository.findAllByOwner(principal.getName(), pageable);

		// remove parent's parent to simplify things for conversion into json..
		StreamSupport.stream(groups.spliterator(), false) // get stream
				.filter(g -> g.getParent() != null) // filter groups with no parent
				.forEach(g -> g.getParent().setParent(null)); // set parent's parent to null

		return groups;
	}

	@RequestMapping(value = "/update/{id}/add-subgroup", method = RequestMethod.PUT)
	public String addSubgroup(@PathVariable long id, @ModelAttribute Group addGroup, Errors result, Principal user) {
		if (addGroup.getId() == id) {
			return "redirect:/groups/show/" + id;
		}

		Group group = groupRepository.findOne(id);
		Group subgroup = groupRepository.findOne(addGroup.getId());

		if (!group.getOwner().equals(user.getName()) || !subgroup.getOwner().equals(user.getName())) {
			return "redirect:/";
		}

		if (!result.hasErrors()) {
			subgroup.setParent(group);
			groupRepository.save(subgroup);
		}

		return "redirect:/groups/show/" + group.getId();
	}

	@RequestMapping(value = "/group", method = RequestMethod.POST)
	public String group(@ModelAttribute GroupRequest groupRequest, Errors binding) {
		if (!binding.hasErrors()) {
			groupService.createGroup(groupRequest);

			return "redirect:/";
		}

		return view("group");
	}

	@RequestMapping(value = "/byTypes", method = RequestMethod.GET)
	@ResponseBody
	public Page<Sample> byType(@RequestParam("types") Set<String> types, Pageable pageable, Principal user) {
		return groupRepository.findAllByTypeValueInAndOwner(types, user.getName(), pageable);
	}

	/**
	 * Gets the first sample's in the group request
	 *
	 * @param groupRequest
	 */
	private void setGroupRequestType(@NotNull GroupRequest groupRequest) {
		final String barcode = groupRequest.getSamples().stream().findFirst().get();
		final Sample sample = sampleService.getRepository().findByBarcode(barcode);
		groupRequest.setType(sample.getType());
	}

}
