package uk.ac.sanger.mig.proto.aker.controllers;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import uk.ac.sanger.mig.proto.aker.entities.Label;
import uk.ac.sanger.mig.proto.aker.entities.Sample;
import uk.ac.sanger.mig.proto.aker.repositories.LabelRepository;
import uk.ac.sanger.mig.proto.aker.repositories.SampleRepository;
import uk.ac.sanger.mig.proto.aker.seeders.SampleSeeder;

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

	@RequestMapping("/{barcode}")
	public String index(@PathVariable("barcode") String barcode, Model model) {
		final List<Sample> byBarcode = sampleRepository.findByBarcode(barcode);
		model.addAttribute("samples", byBarcode);

		return view(Action.INDEX);
	}

	@RequestMapping("/by-type/{type}")
	public String byType(@PathVariable("type") long typeId, Model model) {
		final List<Sample> samples = sampleRepository.findByTypeId(typeId);
		model.addAttribute("samples", samples);

		return view(Action.INDEX);
	}

	@RequestMapping("/create")
	public String create() {
		return view(Action.CREATE);
	}

	@RequestMapping("/add-label/")
	public String addLabel(
			@RequestParam("barcode") String barcode,
			@RequestParam("label") String label
			) {

		final Sample sample = sampleRepository.findOneByBarcode(barcode);
		Label l = new Label();

		l.setId(sample.getId());
		l.setName(label);

		if (labelRepository.save(l) != null) {
			return "redirect:/samples/?success";
		}

		return "redirect:/samples/?error";
	}

}
