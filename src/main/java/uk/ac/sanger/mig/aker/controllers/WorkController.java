package uk.ac.sanger.mig.aker.controllers;

import java.io.File;
import java.io.IOException;
import java.security.Principal;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import uk.ac.sanger.mig.aker.domain.requests.OrderRequest;
import uk.ac.sanger.mig.aker.services.WorkOrderService;

/**
 * @author pi1
 * @since February 2015
 */
@Controller
@RequestMapping("/work")
public class WorkController extends BaseController {

	@Resource
	private WorkOrderService workOrderService;

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
	public Boolean bindOrder(@RequestBody OrderRequest newOrder, HttpSession session) {
		workOrderService.processOrder(newOrder);
		session.setAttribute("order", newOrder);

		return newOrder.isProcessed();
	}

	@RequestMapping(value = "/order", method = RequestMethod.GET)
	public String order(Model model, Principal principal, HttpSession session) {
		OrderRequest order = (OrderRequest) session.getAttribute("order");
		if (order == null || !order.isProcessed()) {
			return "redirect:/work/";
		}

		model.addAttribute("workOrder", order);
		model.addAttribute("principal", principal);

		return view("order");
	}

	@RequestMapping(value = "/clear", method = RequestMethod.GET)
	@ResponseBody
	public Boolean clear(HttpSession session) {
		session.removeAttribute("order");
		return true;
	}

	@RequestMapping(value = "/submit", method = RequestMethod.POST)
	@ResponseBody
	public OrderRequest submit(@ModelAttribute OrderRequest update, HttpSession session) {
		OrderRequest order = (OrderRequest) session.getAttribute("order");
		order.setSamples(update.getSamples());

		return order;
	}

	@RequestMapping(value = "/update", method = RequestMethod.POST)
	@ResponseBody
	public String update(@ModelAttribute OrderRequest order, HttpSession session) {
		workOrderService.update(order);
		return "";
	}

	@RequestMapping(value = "/csv", method = RequestMethod.GET, produces = "text/csv")
	@ResponseBody
	public FileSystemResource generateCsv(HttpServletResponse response, HttpSession session) {
		OrderRequest order = (OrderRequest) session.getAttribute("order");
		try {
			final File file = workOrderService.printOrder(order);
			response.addHeader("Content-Disposition", "attachment; filename=" + file.getName());

			return new FileSystemResource(file.getAbsolutePath());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}
