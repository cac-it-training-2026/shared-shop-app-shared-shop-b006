package jp.co.sss.shop.controller.client.order;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpSession;
import jp.co.sss.shop.form.OrderForm;
import jp.co.sss.shop.repository.OrderRepository;

@Controller
public class ClientOrderRegistController {

	@Autowired
	HttpSession session;

	@Autowired
	OrderRepository orderRepository;

	@ModelAttribute(value = "orderForm")
	public OrderForm createDeliveryForm() {
		return new OrderForm();

	}

	//	
	//	@RequestMapping(path="/client/order/address/input",method=RequestMethod.POST)
	//	String addressInput() {
	//		return "redirect:/client/order/address/input ";
	//	}
	@RequestMapping(path = "/client/order/address/input")
	String addressInput() {
		return "client/order/address_input";
	}

}
