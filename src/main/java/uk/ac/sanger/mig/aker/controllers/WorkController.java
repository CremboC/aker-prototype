package uk.ac.sanger.mig.aker.controllers;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import uk.ac.sanger.mig.aker.domain.WorkOrder;
import uk.ac.sanger.mig.aker.services.OrderService;

/**
 * @author pi1
 * @since February 2015
 */
@Controller
@RequestMapping("/work")
public class WorkController extends BaseController {

	@Resource
	private OrderService orderService;

	@Autowired
	private WorkOrder order;

	@PostConstruct
	private void init() {
		setTemplatePath("work");
	}

	@RequestMapping("/")
	public String index() {
		return view(Action.INDEX);
	}

	@RequestMapping(value = "/order", method = RequestMethod.POST, consumes = "application/json")
	@ResponseBody
	public Boolean bindOrder(@RequestBody WorkOrder order) {
		orderService.processOrder(order);
		this.order = order;
		return order.isProcessed();
	}

	@RequestMapping(value = "/order", method = RequestMethod.GET)
	public String order(Model model) {
		List<String> headers = new ArrayList<>();
		headers.add("Barcode");

		model.addAttribute("order", this.order);
		model.addAttribute("headers", headers);

		return view("order");
	}

}
