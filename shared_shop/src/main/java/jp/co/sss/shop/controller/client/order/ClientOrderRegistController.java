package jp.co.sss.shop.controller.client.order;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
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
import jp.co.sss.shop.bean.BasketBean;
import jp.co.sss.shop.bean.UserBean;
import jp.co.sss.shop.entity.Item;
import jp.co.sss.shop.entity.Order;
import jp.co.sss.shop.entity.OrderItem;
import jp.co.sss.shop.entity.User;
import jp.co.sss.shop.form.OrderForm;
import jp.co.sss.shop.repository.ItemRepository;
import jp.co.sss.shop.repository.OrderItemRepository;
import jp.co.sss.shop.repository.OrderRepository;
import jp.co.sss.shop.repository.UserRepository;

@Controller
@SessionAttributes(value = "orderForm")
public class ClientOrderRegistController {

	@Autowired
	HttpSession session;

	@Autowired
	OrderRepository orderRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	ItemRepository itemRepository;

	@Autowired
	OrderItemRepository orderItemRepository;

	@ModelAttribute(value = "orderForm")
	public OrderForm createDeliveryForm() {
		return new OrderForm();
	}

	@RequestMapping(path = "/client/order/address/input", method = RequestMethod.POST)
	public String addressInput() {
		UserBean loginUser = (UserBean) session.getAttribute("user");
		User user = userRepository.getReferenceById(loginUser.getId());
		OrderForm orderForm = new OrderForm();

		orderForm.setId(user.getId());
		orderForm.setPostalCode(user.getPostalCode());
		orderForm.setAddress(user.getAddress());
		orderForm.setName(user.getName());
		orderForm.setPhoneNumber(user.getPhoneNumber());
		orderForm.setPayMethod(1);

		session.setAttribute("orderForm", orderForm);
		return "redirect:/client/order/address/input";
	}

	@RequestMapping(path = "/client/order/address/input", method = RequestMethod.GET)
	public String addressInput(@ModelAttribute("orderForm") OrderForm form, Model model) {
		OrderForm orderForm = (OrderForm) session.getAttribute("orderForm");
		model.addAttribute("orderForm", orderForm);

		if (session.getAttribute("errors") != null) {
			model.addAttribute("errors", session.getAttribute("errors"));
			session.removeAttribute("errors");
		}
		return "client/order/address_input";
	}

	@RequestMapping(path = "/client/order/payment/input", method = RequestMethod.POST)
	public String paymentInput(@Validated @ModelAttribute("orderForm") OrderForm form, BindingResult result) {
		if (result.hasErrors()) {
			session.setAttribute("errors", result);
			System.out.println("今日");
			return "redirect:/client/order/address/input";
		}
		session.setAttribute("orderForm", form);
		return "redirect:/client/order/payment/input";
	}

	@RequestMapping(path = "/client/order/payment/input", method = RequestMethod.GET)
	public String paymentInput(Model model) {
		OrderForm orderForm = (OrderForm) session.getAttribute("orderForm");
		model.addAttribute("orderForm", orderForm);
		model.addAttribute("payMethod", orderForm.getPayMethod());
		return "client/order/payment_input";
	}

	@RequestMapping(path = "/client/order/check", method = RequestMethod.POST)
	public String orderCheckPost(OrderForm form) {
		OrderForm orderForm = (OrderForm) session.getAttribute("orderForm");
		orderForm.setPayMethod(form.getPayMethod());
		session.setAttribute("orderForm", orderForm);
		return "redirect:/client/order/check";
	}

	@RequestMapping(path = "/client/order/check", method = RequestMethod.GET)
	public String orderCheck(Model model) {
		OrderForm orderForm = (OrderForm) session.getAttribute("orderForm");
		model.addAttribute("orderForm", orderForm);

		@SuppressWarnings("unchecked")
		List<BasketBean> basketList = (List<BasketBean>) session.getAttribute("basketBeans");
		
		if (basketList == null || basketList.isEmpty()) {
			model.addAttribute("orderItemBeans", null);
			return "client/order/check";
		}

		List<String> itemNameListLessThan = new ArrayList<>();
		List<String> itemNameListZero = new ArrayList<>();

		for (int i = basketList.size() - 1; i >= 0; i--) {
			BasketBean basketBean = basketList.get(i);
			Item item = itemRepository.findById(basketBean.getId()).orElse(null);

			if (item == null || item.getStock() == 0) {
				itemNameListZero.add(basketBean.getName());
				basketList.remove(i);
			} else if (item.getStock() < basketBean.getOrderNum()) {
				itemNameListLessThan.add(basketBean.getName());
				basketBean.setOrderNum(item.getStock());
			}
		}
		
		if (!itemNameListLessThan.isEmpty()) {
			model.addAttribute("itemNameListLessThan", itemNameListLessThan);
		}
		if (!itemNameListZero.isEmpty()) {
			model.addAttribute("itemNameListZero", itemNameListZero);
		}
		
		session.setAttribute("basketBeans", basketList);

		if (basketList.isEmpty()) {
			model.addAttribute("orderItemBeans", null);
		} else {
			int sum = 0;
			for (BasketBean basketBean : basketList) {
				Item item = itemRepository.getReferenceById(basketBean.getId());
				sum += basketBean.getOrderNum() * item.getPrice();
			}
			model.addAttribute("orderItemBeans", basketList);
			model.addAttribute("total", sum);
		}
		
		return "client/order/check";
	}

	@RequestMapping(path = "/client/order/payment/back", method = RequestMethod.POST)
	public String orderBack() {
		return "redirect:/client/order/address/input";
	}

	@RequestMapping(path = "/client/order/complete", method = RequestMethod.POST)
	public String orderComplete(OrderForm form) {
		OrderForm orderForm = (OrderForm) session.getAttribute("orderForm");

		@SuppressWarnings("unchecked")
		List<BasketBean> basketList = (List<BasketBean>) session.getAttribute("basketBeans");

		for (BasketBean basketBean : basketList) {
			Item item = itemRepository.findById(basketBean.getId()).orElse(null);
			if (item == null || item.getStock() < basketBean.getOrderNum()) {
				return "redirect:/client/order/check";
			}
		}

		Order order = new Order();
		User user = userRepository.getReferenceById(orderForm.getId());
		BeanUtils.copyProperties(orderForm, order, "id", "insertDate", "orderItemsList", "user");
		order.setInsertDate(Date.valueOf(LocalDate.now()));
		order.setUser(user);
		order = orderRepository.save(order);

		for (BasketBean basketBean : basketList) {
			OrderItem orderItem = new OrderItem();
			Item item = itemRepository.getReferenceById(basketBean.getId());

			orderItem.setOrder(order);
			orderItem.setItem(item);
			orderItem.setQuantity(basketBean.getOrderNum());
			orderItem.setPrice(item.getPrice());
			orderItemRepository.save(orderItem);

			item.setStock(item.getStock() - basketBean.getOrderNum());
			itemRepository.save(item);
		}

		session.removeAttribute("basketBeans");
		session.removeAttribute("orderForm");

		return "redirect:/client/order/complete";
	}

	@RequestMapping(path = "/client/order/complete", method = RequestMethod.GET)
	public String orderComplete() {
		return "client/order/complete";
	}
}