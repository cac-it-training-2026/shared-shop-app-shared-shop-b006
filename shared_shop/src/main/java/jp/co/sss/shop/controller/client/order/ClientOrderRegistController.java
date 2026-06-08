package jp.co.sss.shop.controller.client.order;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;

import jakarta.servlet.http.HttpSession;
import jp.co.sss.shop.form.OrderForm;
import jp.co.sss.shop.repository.OrderRepository;

@Controller
@SessionAttributes(value = "orderForm")
public class ClientOrderRegistController {

	@Autowired
	HttpSession session;

	@Autowired
	OrderRepository orderRepository;

	@ModelAttribute(value = "orderForm")
	public OrderForm createDeliveryForm() {
		return new OrderForm();

	}

	@RequestMapping(path = "/client/order/address/input", method = RequestMethod.POST)
	String addressInput() {
		return "redirect:/client/order/address/input ";
	}

	@RequestMapping(path = "/client/order/address/input")
	String addressInput(@Validated @ModelAttribute("orderForm") OrderForm form, BindingResult result, Model model) {
		if (result.hasErrors()) {
			model.addAttribute("fields", result);
			session.removeAttribute("orderForm");
		}
		return "client/order/address_input";
	}

}
