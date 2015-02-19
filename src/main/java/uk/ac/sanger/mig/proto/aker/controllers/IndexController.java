package uk.ac.sanger.mig.proto.aker.controllers;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import uk.ac.sanger.mig.proto.aker.messages.Order;
import uk.ac.sanger.mig.proto.aker.services.OrderService;

import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * @author pi1
 * @since February 2015
 */
@Controller
public class IndexController extends BaseController {

	@Resource
	private OrderService orderService;

	@PostConstruct
	private void init() {
		setTemplatePath("main");
	}

	@RequestMapping("/")
	public String greeting(@RequestParam(value = "name", required = false, defaultValue = "World") String name,
			Model model) {
		model.addAttribute("name", name);

		return view(Action.INDEX);
	}

	@RequestMapping("/rabbit")
	public String rabbit() throws InterruptedException, JsonProcessingException {

		final Order test = new Order("Hello from RabbitMQ!");
		final Order test2 = new Order("Hello from RabbitMQ! 2");
		final Order test3 = new Order("Hello from RabbitMQ! 3");

		orderService.sendOrder(test);
		orderService.sendOrder(test2);
		orderService.sendOrder(test3);

		return view(Action.INDEX);
	}

}
