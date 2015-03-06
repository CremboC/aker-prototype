package uk.ac.sanger.mig.aker.controllers;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
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
@Scope("session")
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
	public Boolean bindOrder(@RequestBody WorkOrder newOrder) {
		orderService.processOrder(newOrder);
		order = newOrder;

		return newOrder.isProcessed();
	}

	@RequestMapping(value = "/order", method = RequestMethod.GET)
	public String order(Model model) {
		model.addAttribute("workOrder", order);

		return view("order");
	}

	@RequestMapping(value = "/submit", method = RequestMethod.PUT)
	@ResponseBody
	public WorkOrder submit(@ModelAttribute WorkOrder update) {
		order.setSamples(update.getSamples());

		return order;
	}

}
