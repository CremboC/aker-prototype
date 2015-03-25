package uk.ac.sanger.mig.aker.controllers;

import java.security.Principal;
import java.util.Collection;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import uk.ac.sanger.mig.aker.services.OrderService;

/**
 * @author pi1
 * @since March 2015
 */
@Controller
@RequestMapping("/orders")
public class OrdersController extends BaseController {

	@Resource
	private OrderService orderService;

	@PostConstruct
	private void init() {
		setTemplatePath("orders");
	}

	@RequestMapping(value = "/get", method = RequestMethod.GET)
	@ResponseBody
	private Collection<Object> orders(Principal principal) {
		// TODO: return proper error message
		return orderService.queryOrders(principal.getName());
	}

	@RequestMapping(value = "/get/{id}", method = RequestMethod.GET)
	@ResponseBody
	private Map<String, Object> order(@PathVariable Long id, Principal principal) {
		// TODO: return proper error message
		return orderService.queryOrder(id, principal.getName());
	}

	@RequestMapping("/")
	private String index() {
		return view(Action.INDEX);
	}

	@RequestMapping("/show/{id}")
	private String show(@PathVariable Long id, Model model) {
		model.addAttribute("id", id);
		return view(Action.SHOW);
	}
}
