package uk.ac.sanger.mig.aker.controllers;

import java.security.Principal;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import uk.ac.sanger.mig.aker.domain.Group;
import uk.ac.sanger.mig.aker.domain.Sample;
import uk.ac.sanger.mig.aker.domain.requests.GroupRequest;
import uk.ac.sanger.mig.aker.domain.requests.Response;
import uk.ac.sanger.mig.aker.domain.requests.SampleGroup;
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
		// TODO: create group validator
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
	public ModelAndView store(
			@Valid @ModelAttribute("groupRequest") GroupRequest groupRequest,
			Errors errors,
			Principal principal,
			RedirectAttributes attributes) {
		final ModelAndView mav = new ModelAndView();
		if (errors.hasErrors()) {
			mav.setViewName(view(Action.CREATE));
			return mav;
		}

		setGroupType(groupRequest, principal.getName());
		final Optional<Group> group = groupService.createGroup(groupRequest);

		if (!group.isPresent()) {
			mav.setViewName(redirect("create"));
			attributes.addFlashAttribute("status", new Response(Response.Status.FAIL, "There was a problem creating the group"));
			return mav;
		} else {
			mav.setViewName(redirect("show", group.get().getId()));
			return mav;
		}
	}

	@RequestMapping(value = "/show/{id}", method = RequestMethod.GET)
	public ModelAndView show(@PathVariable Long id, Model model, Principal user, RedirectAttributes attributes) {
		final ModelAndView mav = new ModelAndView();
		final Group group = groupRepository.findOne(id);

		if (!group.getOwner().equals(user.getName())) {
			attributes.addFlashAttribute("error", new Response(Response.Status.FAIL, "Illegal operation"));
			mav.setViewName(redirect("/groups/"));
			return mav;
		}

		group.setChildren(groupRepository.findByParentId(group.getId()));

		final Collection<Group> groups = otherGroups(group, user.getName());

		model.addAttribute("group", group);
		model.addAttribute("subgroup", new Group());
		model.addAttribute("groups", groups);

		mav.setViewName(view(Action.SHOW));
		return mav;
	}

	@RequestMapping(value = "/edit/{id}", method = RequestMethod.GET)
	public String edit(@PathVariable Long id, Model model, Principal user) {
		final Group group = groupRepository.findOne(id);

		if (!group.getOwner().equals(user.getName())) {
			// illegal state, no need to handle gently
			return redirect("/");
		}

		group.setChildren(groupRepository.findByParentId(group.getId()));

		final Collection<Group> groups = otherGroups(group, user.getName());

		model.addAttribute("group", group);
		model.addAttribute("subgroup", new Group());
		model.addAttribute("samples", new SampleGroup());
		model.addAttribute("groups", groups);

		return view(Action.EDIT);
	}

	@RequestMapping(value = "/update/{id}", method = RequestMethod.PUT)
	public ModelAndView update(
			@PathVariable Long id,
			@RequestParam(value = "samples[]", required = false) Collection<String> samples,
			@ModelAttribute Group group,
			Errors errors,
			Principal principal,
			RedirectAttributes attributes) {

		if (errors.hasErrors()) {
			attributes.addFlashAttribute("status", new Response(Response.Status.FAIL, "An error has occurred: " + errors.getAllErrors()));
			return new ModelAndView(redirect("edit", id));
		}

		final Group storedGroup = groupRepository.findOne(id);

		if (storedGroup == null || !storedGroup.getOwner().equals(principal.getName())) {
			// illegal condition, shouldn't even be possible so no need to handle this gently
			return new ModelAndView(redirect("/groups/"));
		}

		groupService.save(group);

		attributes.addFlashAttribute("status", new Response(Response.Status.SUCCESS, "Successfully updated storedGroup."));

		return new ModelAndView(redirect("show", id));
	}

	// TODO: make DELETE. For the sake of simplicity, to make deletion work as a simple URL, GET is used
	@RequestMapping(value = "/delete/{id}", method = RequestMethod.GET)
	public ModelAndView delete(@PathVariable Long id, Principal principal, RedirectAttributes attributes) {
		boolean deleted = groupService.delete(id, principal.getName());

		if (!deleted) {
			// only fails to delete if owner is not matched or group doesn't exist
			attributes.addFlashAttribute("status",
					new Response(Response.Status.FAIL, "Doesn't exist or illegal operation."));
			return new ModelAndView("redirect:/groups/");
		}

		attributes.addFlashAttribute("status", new Response(Response.Status.SUCCESS, "Successfully deleted group."));

		return new ModelAndView(redirect("/groups/"));
	}

	@RequestMapping(value = "/json", method = RequestMethod.GET)
	@ResponseBody
	public Page<Group> json(Pageable pageable, Principal principal) {
		return groupService.allByOwner(principal.getName(), pageable);
	}

	@RequestMapping(value = "/update/{id}/add-subgroup", method = RequestMethod.PUT)
	public String addSubgroup(@PathVariable long id, @ModelAttribute Group addGroup, Errors result, Principal user) {
		if (addGroup.getId() == id) {
			return redirect("show", id);
		}

		Group group = groupRepository.findOne(id);
		Group subgroup = groupRepository.findOne(addGroup.getId());

		if (!group.getOwner().equals(user.getName()) || !subgroup.getOwner().equals(user.getName())) {
			return redirect("/");
		}

		if (!result.hasErrors()) {
			subgroup.setParent(group);
			groupRepository.save(subgroup);
		}

		return redirect("show", id);
	}

	@RequestMapping(value = "/group", method = RequestMethod.POST)
	public String group(@ModelAttribute GroupRequest groupRequest, Errors binding) {
		if (!binding.hasErrors()) {
			final Group group = groupService.createGroup(groupRequest).orElseThrow(IllegalStateException::new);

			return redirect("show", group.getId());
		}

		return view("group");
	}

	@RequestMapping(value = "/byTypes", method = RequestMethod.GET)
	@ResponseBody
	public Page<Sample> byType(@RequestParam("types") Set<String> types, Pageable pageable, Principal user) {
		return groupRepository.findAllByTypeValueInAndOwner(types, user.getName(), pageable);
	}

	/**
	 * Gets groups which can be either a parent or a subgroup
	 *
	 * @param group the group subject
	 * @param owner owner of groups
	 * @return legal groups
	 */
	private Collection<Group> otherGroups(Group group, String owner) {
		return groupRepository
				.findAllByIdNotAndOwner(group.getId(), owner)
				.stream()
				.filter(g -> g != group.getParent() && !group.getChildren().contains(g))
				.collect(Collectors.toList());
	}

	/**
	 * Gets the first sample's in the group request
	 *
	 * @param groupRequest
	 * @param owner
	 */
	private void setGroupType(@NotNull GroupRequest groupRequest, String owner) {
		final String barcode = groupRequest.getSamples().stream().findFirst().get();
		// TODO: gentle handling of sample not found
		final Sample sample = sampleService.byBarcode(barcode, owner).orElseThrow(IllegalStateException::new);
		groupRequest.setType(sample.getType());
	}

}
