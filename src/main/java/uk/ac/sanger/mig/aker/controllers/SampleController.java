package uk.ac.sanger.mig.aker.controllers;

import java.security.Principal;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import uk.ac.sanger.mig.aker.domain.Alias;
import uk.ac.sanger.mig.aker.domain.Group;
import uk.ac.sanger.mig.aker.domain.Sample;
import uk.ac.sanger.mig.aker.domain.Tag;
import uk.ac.sanger.mig.aker.domain.Type;
import uk.ac.sanger.mig.aker.domain.requests.GroupRequest;
import uk.ac.sanger.mig.aker.domain.requests.SampleRequest;
import uk.ac.sanger.mig.aker.repositories.AliasRepository;
import uk.ac.sanger.mig.aker.repositories.SampleRepository;
import uk.ac.sanger.mig.aker.repositories.TagRepository;
import uk.ac.sanger.mig.aker.services.GroupService;
import uk.ac.sanger.mig.aker.services.SampleService;
import uk.ac.sanger.mig.aker.services.TypeService;
import uk.ac.sanger.mig.aker.utils.SampleHelper;

/**
 * @author pi1
 * @since February 2015
 */
@Controller
@RequestMapping("/samples")
public class SampleController {

	@Autowired
	private AliasRepository aliasRepository;

	@Autowired
	private TagRepository tagRepository;

	@Autowired
	private SampleService sampleService;

	@Autowired
	private TypeService typeService;

	@Autowired
	private GroupService groupService;

	private SampleRepository sampleRepository;

	@PostConstruct
	private void init() {
		sampleRepository = sampleService.getRepository();
	}

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String index(Model model) {
		model.addAttribute("groupRequest", new GroupRequest());

		return "samples/index";
	}

	@RequestMapping(value = "/json", method = RequestMethod.GET)
	@ResponseBody
	public Page<Sample> json(Pageable p) {
		return sampleService.findAll(p);
	}

	@RequestMapping("/show/{barcode}")
	public String show(@PathVariable("barcode") String barcode, Model model, Principal user) {
		final Optional<Sample> sample = sampleService.findByBarcode(barcode, user.getName());
		if (sample.isPresent()) {
			model.addAttribute("sample", sample.get());
		} else {
			// TODO: proper not found
			return "redirect:/samples/?404";
		}

		model.addAttribute("alias", new Alias());
		model.addAttribute("tag", new Tag());

		return "samples/show";
	}

	@RequestMapping("/create")
	public String create(Model model) {
		model.addAttribute("types", typeService.findAll());
		model.addAttribute("sampleRequest", new SampleRequest());

		return "sample/create";
	}

	@RequestMapping(value = "/store", method = RequestMethod.POST)
	public String store(
			@Valid @ModelAttribute SampleRequest request,
			Errors bindingResult,
			Model model,
			Principal principal) {
		if (bindingResult.hasErrors()) {
			model.addAttribute("types", typeService.findAll());
			return "samples/create";
		}

		sampleService.createSamples(request, principal.getName());
		return "redirect:/samples/";
	}

	@RequestMapping(value = "/update/{barcode}/add-alias", method = RequestMethod.PUT)
	public String addAlias(
			@PathVariable("barcode") String barcode,
			@Valid @ModelAttribute Alias alias,
			Principal user,
			Errors bindingResult) {
		final Optional<Sample> optSample = sampleService.findByBarcode(barcode, user.getName());

		if (optSample.isPresent()) {
			final Sample sample = optSample.get();

			if (!bindingResult.hasErrors()) {
				alias.setSample(sample);
				aliasRepository.save(alias);
			}

			return "redirect:/samples/show/" + sample.getBarcode();
		}

		return "redirect:/samples/?404" + optSample.toString();
	}

	@RequestMapping(value = "/update/{barcode}/add-tag", method = RequestMethod.PUT)
	public String addTag(
			@PathVariable("barcode") String barcode,
			@Valid @ModelAttribute Tag tag,
			Principal user,
			Errors bindingResult) {
		final Optional<Sample> optSample = sampleService.findByBarcode(barcode, user.getName());

		if (optSample.isPresent()) {
			final Sample sample = optSample.get();

			if (!bindingResult.hasErrors()) {
				tag.setSample(sample);
				tagRepository.save(tag);
			}

			return "samples/show/" + sample.getBarcode();
		}

		return "redirect:/samples/?404" + optSample.toString();
	}

	@RequestMapping(value = "/group", method = RequestMethod.POST)
	public String group(@ModelAttribute GroupRequest groupRequest, Errors binding, Principal principal) {
		if (!binding.hasErrors() && !groupRequest.getSamples().isEmpty()) {

			final Collection<Sample> samples = sampleRepository.findAll(SampleHelper.idFromBarcode(groupRequest.getSamples()));

			final Type type = samples.stream().findFirst().orElseThrow(IllegalStateException::new).getType();
			final boolean singleType = samples.stream().map(Sample::getType).distinct().count() == 1;

			if (singleType) {
				groupRequest.setType(type);

				// TODO: handle failure to create group
				final Group group = groupService.createGroup(groupRequest, principal.getName()).orElse(null);

				return "redirect:/groups/show/" + group.getId();
			} else {
				throw new IllegalStateException("Shouldn't be possible to select samples of multiple types");
			}
		}

		return "groups/group";
	}

	@RequestMapping(value = "/byGroup/{groupId}", method = RequestMethod.GET)
	@ResponseBody
	public Page<Sample> byGroupPaged(@PathVariable long groupId, Pageable pageable, Principal user) {
		return sampleService.findByGroup(groupId, user.getName(), pageable);
	}

	@RequestMapping(value = "/byGroups", method = RequestMethod.GET)
	@ResponseBody
	public Set<Sample> byGroups(@RequestParam("groups") Collection<Long> groupIds) {
		return sampleRepository.findAllByGroupsIdIn(groupIds);
	}

	@RequestMapping(value = "/byTypes", method = RequestMethod.GET)
	@ResponseBody
	public Page<Sample> byType(@RequestParam("types") Set<String> types, Pageable pageable, Principal owner) {
		return sampleService.findByType(types, owner.getName(), pageable);
	}

	@RequestMapping(value = "/byBarcodes", method = RequestMethod.GET)
	@ResponseBody
	public Collection<Sample> byBarcode(
			@RequestParam(value = "barcodes", required = false) Collection<String> barcodes,
			Principal owner) {
		if (barcodes.isEmpty()) {
			return null;
		}
		return sampleService.findByBarcode(barcodes, owner.getName());
	}

}
