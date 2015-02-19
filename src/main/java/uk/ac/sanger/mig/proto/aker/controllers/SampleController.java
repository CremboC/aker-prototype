package uk.ac.sanger.mig.proto.aker.controllers;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.commons.lang3.text.WordUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import uk.ac.sanger.mig.proto.aker.entities.Label;
import uk.ac.sanger.mig.proto.aker.entities.Sample;
import uk.ac.sanger.mig.proto.aker.entities.SampleRequest;
import uk.ac.sanger.mig.proto.aker.entities.Type;
import uk.ac.sanger.mig.proto.aker.repositories.LabelRepository;
import uk.ac.sanger.mig.proto.aker.repositories.SampleRepository;
import uk.ac.sanger.mig.proto.aker.repositories.TypeRepository;
import uk.ac.sanger.mig.proto.aker.seeders.SampleSeeder;
import uk.ac.sanger.mig.proto.aker.services.SampleService;

/**
 * @author pi1
 * @since February 2015
 */
@Controller
@RequestMapping("/samples")
public class SampleController extends BaseController {

	@Autowired
	private SampleRepository sampleRepository;

	@Autowired
	private LabelRepository labelRepository;

	@Autowired
	private TypeRepository typeRepository;

	@Resource
	private SampleService sampleService;

	@Autowired
	private SampleSeeder seeder;

	@PostConstruct
	private void init() {
		setTemplatePath("samples");
	}

	@RequestMapping("/seed")
	@ResponseBody
	public String seed() {
		seeder.seed();
		return "Done";
	}

	@RequestMapping("/")
	public String index(Model model) {
		final Iterable<Sample> samples = sampleRepository.findAll();
		model.addAttribute("samples", samples);

		return view(Action.INDEX);
	}

	@RequestMapping("/show/{barcode}")
	public String show(@PathVariable("barcode") String barcode, Model model) {
		final List<Sample> byBarcode = sampleRepository.findByBarcode(barcode);
		model.addAttribute("samples", byBarcode);

		return view(Action.INDEX);
	}

	@RequestMapping("/{type}")
	public String byType(@PathVariable("type") long typeId, Model model) {
		final List<Sample> samples = sampleRepository.findByTypeId(typeId);
		model.addAttribute("samples", samples);

		return view(Action.INDEX);
	}

	@RequestMapping("/create")
	public String create(Model model) {
		final Iterable<Type> all = typeRepository.findAll();
		all.forEach(type -> type.setName(WordUtils.capitalizeFully(type.getName())));

		model.addAttribute("types", all);
		model.addAttribute("sampleRequest", new SampleRequest());

		return view(Action.CREATE);
	}

	@RequestMapping(value = "/store", method = RequestMethod.POST)
	public String store(@ModelAttribute SampleRequest request, Model model) {
		model.addAttribute("request", request);
		sampleService.createSamples(request);

		return "redirect:/samples/";
	}

	@RequestMapping(value = "/{barcode}/add-label/")
	public String addLabel(@PathVariable("barcode") String barcode) {
		final Sample sample = sampleRepository.findOneByBarcode(barcode);

		Label l = new Label();
		l.setName("Added Label");
		l.setSample(sample);

		if (labelRepository.save(l) != null) {
			return "redirect:/samples/?success";
		}

		return "redirect:/samples/?error";
	}

}
