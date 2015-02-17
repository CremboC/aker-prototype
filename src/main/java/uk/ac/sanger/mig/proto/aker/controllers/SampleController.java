package uk.ac.sanger.mig.proto.aker.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import uk.ac.sanger.mig.proto.aker.entities.Sample;
import uk.ac.sanger.mig.proto.aker.repositories.SampleRepository;
import uk.ac.sanger.mig.proto.aker.seeders.SampleSeeder;

/**
 * @author pi1
 * @since February 2015
 */
@Controller
@RequestMapping("/samples")
public class SampleController {

	@Autowired
	private SampleRepository sampleRepository;

	@Autowired
	private SampleSeeder seeder;

	@RequestMapping("/{barcode}")
	public String index(@PathVariable("barcode") String barcode, Model model) {
		final List<Sample> byBarcode = sampleRepository.findByBarcode(barcode);
		model.addAttribute("samples", byBarcode);

		return "samples/index";
	}

	@RequestMapping("/by-type/{type}")
	public String byType(@PathVariable("type") long typeId, Model model) {
		final List<Sample> samples = sampleRepository.findByTypeId(typeId);
		model.addAttribute("samples", samples);

		return "samples/index";
	}

	@RequestMapping("/create")
	public String create() {
		return "samples/create";
	}

}
