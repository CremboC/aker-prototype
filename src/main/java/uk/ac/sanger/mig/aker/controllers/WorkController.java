package uk.ac.sanger.mig.aker.controllers;

import java.io.File;
import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import uk.ac.sanger.mig.aker.domain.OrderRequest;
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
	private OrderRequest order;

	@PostConstruct
	private void init() {
		setTemplatePath("work");
	}

	@RequestMapping("/")
	public String index() {
		return view(Action.INDEX);
	}

	@RequestMapping(value = "/order", method = RequestMethod.GET)
	public String order(Model model) {
		if (order == null || !order.isProcessed()) {
			return "redirect:/work/";
		}

		model.addAttribute("workOrder", order);

		return view("order");
	}

	@RequestMapping(value = "/order", method = RequestMethod.POST, consumes = "application/json")
	@ResponseBody
	public Boolean bindOrder(@RequestBody OrderRequest newOrder) {
		orderService.processOrder(newOrder);
		order = newOrder;

		return newOrder.isProcessed();
	}

	@RequestMapping(value = "/clear", method = RequestMethod.GET)
	@ResponseBody
	public Boolean clear() {
		this.order = null;
		return true;
	}

	@RequestMapping(value = "/submit", method = RequestMethod.POST)
	@ResponseBody
	public OrderRequest submit(@ModelAttribute OrderRequest update) {
		order.setSamples(update.getSamples());

		return order;
	}

	@RequestMapping(value = "/csv", method = RequestMethod.GET, produces = "text/csv")
	@ResponseBody
	public FileSystemResource generateCsv(HttpServletResponse response) {
		try {
			final File file = orderService.printOrder(order);
			response.addHeader("Content-Disposition", "attachment; filename=" + file.getName());

			return new FileSystemResource(file.getAbsolutePath());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}
