package uk.ac.sanger.mig.aker.controllers;

import java.security.Principal;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
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
import uk.ac.sanger.mig.aker.requests.GroupRequest;
import uk.ac.sanger.mig.aker.domain.Sample;
import uk.ac.sanger.mig.aker.requests.SampleRequest;
import uk.ac.sanger.mig.aker.domain.Tag;
import uk.ac.sanger.mig.aker.domain.Type;
import uk.ac.sanger.mig.aker.repositories.AliasRepository;
import uk.ac.sanger.mig.aker.repositories.SampleRepository;
import uk.ac.sanger.mig.aker.repositories.TagRepository;
import uk.ac.sanger.mig.aker.services.GroupService;
import uk.ac.sanger.mig.aker.services.SampleService;
import uk.ac.sanger.mig.aker.services.TypeService;

/**
 * @author pi1
 * @since February 2015
 */
@Controller
@RequestMapping("/samples")
public class SampleController extends BaseController {


	@Autowired
	private AliasRepository aliasRepository;

	@Autowired
	private TagRepository tagRepository;

	@Resource
	private SampleService sampleService;

	@Resource
	private TypeService typeService;

	@Resource
	private GroupService groupService;

	private SampleRepository sampleRepository;

	@PostConstruct
	private void init() {
		setTemplatePath("samples");
		sampleRepository = sampleService.getRepository();

	}

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String index(Model model) {
		model.addAttribute("groupRequest", new GroupRequest());

		return view(Action.INDEX);
	}

	@RequestMapping(value = "/json", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public Page<Sample> json(Pageable p) {
		return sampleService.findAll(p);
	}

	@RequestMapping("/show/{barcode}")
	public String show(@PathVariable("barcode") String barcode, Model model, Principal user) {
		final Optional<Sample> sample = sampleService.findByBarcode(barcode);
		if (sample.isPresent()) {
			model.addAttribute("sample", sample.get());

			if (!sample.get().getOwner().equals(user.getName())) {
				return "redirect:/";
			}

		} else {
			return "redirect:/samples/?404";
		}

		model.addAttribute("alias", new Alias());
		model.addAttribute("tag", new Tag());

		return view(Action.SHOW);
	}

	@RequestMapping("/create")
	public String create(Model model) {
		model.addAttribute("types", typeService.findAll());
		model.addAttribute("sampleRequest", new SampleRequest());

		return view(Action.CREATE);
	}

	@RequestMapping(value = "/store", method = RequestMethod.POST)
	public String store(@Valid @ModelAttribute SampleRequest request, Errors bindingResult, Model model) {
		if (bindingResult.hasErrors()) {
			model.addAttribute("types", typeService.findAll());
			return view(Action.CREATE);
		}

		sampleService.createSamples(request);
		return "redirect:/samples/";
	}

	@RequestMapping(value = "/update/{barcode}/add-alias", method = RequestMethod.PUT)
	public String addAlias(
			@PathVariable("barcode") String barcode,
			@Valid @ModelAttribute Alias alias,
			Principal user,
			Errors bindingResult) {
		final Optional<Sample> optSample = sampleService.findByBarcode(barcode);

		if (optSample.isPresent()) {
			final Sample sample = optSample.get();

			if (!sample.getOwner().equals(user.getName())) {
				return "redirect:/";
			}

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
		final Optional<Sample> optSample = sampleService.findByBarcode(barcode);

		if (optSample.isPresent()) {
			final Sample sample = optSample.get();

			if (!sample.getOwner().equals(user.getName())) {
				return "redirect:/";
			}

			if (!bindingResult.hasErrors()) {
				tag.setSample(sample);
				tagRepository.save(tag);
			}

			return "redirect:/samples/show/" + sample.getBarcode();
		}

		return "redirect:/samples/?404" + optSample.toString();
	}

	@RequestMapping(value = "/group", method = RequestMethod.POST)
	public String group(@ModelAttribute GroupRequest groupRequest, Errors binding) {
		if (!binding.hasErrors()) {
			final Set<Sample> allByBarcodeIn = sampleRepository.findAllByBarcodeIn(groupRequest.getSamples());

			boolean singleType = true;
			Type type = null;
			for (final Sample sample : allByBarcodeIn) {
				if (type == null) {
					type = sample.getType();
				}
				if (!sample.getType().equals(type)) {
					singleType = false;
					break;
				}
			}

			groupRequest.setType(type);

			if (singleType) {
				groupService.createGroup(groupRequest);
			} else {
				throw new IllegalStateException("Shouldn't be possible to select types of multiple types");
			}

			return "redirect:/";
		}

		return view("group");
	}

	@RequestMapping(value = "/byGroup/{groupId}", method = RequestMethod.GET)
	@ResponseBody
	public Page<Sample> byGroupPaged(@PathVariable long groupId, Pageable pageable) {
		return sampleRepository.findAllByGroupsIdIn(groupId, pageable);
	}

	@RequestMapping(value = "/byGroups", method = RequestMethod.GET)
	@ResponseBody
	public Set<Sample> byGroup(@RequestParam("groups") Collection<Long> groupIds) {
		return sampleRepository.findAllByGroupsIdIn(groupIds);
	}


	@RequestMapping(value = "/byTypes", method = RequestMethod.GET)
	@ResponseBody
	public Page<Sample> byType(@RequestParam("types") Set<String> types, Pageable pageable, Principal owner) {
		return sampleRepository.findAllByTypeValueInAndOwner(types, owner.getName(), pageable);
	}

	@RequestMapping(value = "/byBarcodes", method = RequestMethod.GET)
	@ResponseBody
	public Set<Sample> byBarcode(
			@RequestParam(value = "barcodes", required = false) Collection<String> barcodes,
			Principal owner) {
		if (barcodes.isEmpty()) {
			return null;
		}
		return sampleRepository.findAllByBarcodeInAndOwner(barcodes, owner.getName());
	}

}
