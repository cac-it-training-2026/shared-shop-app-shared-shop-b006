package jp.co.sss.shop.controller.client.order;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jakarta.servlet.http.HttpSession;
import jp.co.sss.shop.bean.BasketBean;
import jp.co.sss.shop.bean.UserBean;
import jp.co.sss.shop.entity.Item;
import jp.co.sss.shop.entity.Order;
import jp.co.sss.shop.entity.OrderItem;
import jp.co.sss.shop.entity.MysteryItem;
import jp.co.sss.shop.entity.MysteryItemWeight;
import jp.co.sss.shop.entity.User;
import jp.co.sss.shop.entity.UserCoupon;
import java.time.LocalDate;
import java.sql.Date;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;
import jp.co.sss.shop.form.OrderForm;
import jp.co.sss.shop.repository.ItemRepository;
import jp.co.sss.shop.repository.MysteryItemRepository;
import jp.co.sss.shop.repository.MysteryItemWeightRepository;
import jp.co.sss.shop.repository.OrderItemRepository;
import jp.co.sss.shop.repository.OrderRepository;
import jp.co.sss.shop.repository.UserRepository;
import jp.co.sss.shop.util.Constant;
import jp.co.sss.shop.repository.UserCouponRepository;
import jp.co.sss.shop.service.PriceCalc;

/**
* 注文機能を実装するコントローラー
* @author 高戸
*/
@Controller

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

	@Autowired
	MysteryItemRepository mysteryItemRepository;

	@Autowired
	MysteryItemWeightRepository mysteryItemWeightRepository;

	@Autowired
	UserCouponRepository userCouponRepository;

	@Autowired
	PriceCalc priceCalc;

	//	届け先住所の登録と入力フォームに表示する初期値の設定を行う
	/**
	 * @return
	 */
	@RequestMapping(path = "/client/order/address/input", method = RequestMethod.POST)
	public String addressInput() {
		if (session.getAttribute("user") == null) {

			return "redirect:/login";
		}
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

	//	入力チェックと入力画面の表示を行う
	/**
	 * @param form
	 * @param model
	 * @return
	 */
	@RequestMapping(path = "/client/order/address/input", method = RequestMethod.GET)
	public String addressInput(@ModelAttribute("orderForm") OrderForm form, Model model) {
		OrderForm orderForm = (OrderForm) session.getAttribute("orderForm");
		model.addAttribute("orderForm", orderForm);
		if (session.getAttribute("user") == null) {

			return "redirect:/login";
		}

		//		支払方法選択画面への遷移時に入力にエラーがある場合はここにリダイレクトされるので
		//		そのエラーの内容を表示するためにrequestへの表示とsessionからの削除を行う
		if (session.getAttribute("errors") != null) {
			model.addAttribute(
					"org.springframework.validation.BindingResult.orderForm",
					session.getAttribute("errors"));
			session.removeAttribute("errors");
		}
		return "client/order/address_input";
	}

	//	届け先入力からの遷移時に入力にエラーがある場合はその内容をsessionに保存し届け先入力画面にredirect
	/**
	 * @param form
	 * @param result
	 * @return
	 */
	@RequestMapping(path = "/client/order/payment/input", method = RequestMethod.POST)
	public String paymentInput(@Validated @ModelAttribute("orderForm") OrderForm form, BindingResult result) {
		if (session.getAttribute("user") == null) {

			return "redirect:/login";
		}
		if (result.hasErrors()) {
			session.setAttribute("errors", result);
			return "redirect:/client/order/address/input";
		}
		//		入力エラーがない場合はフォームの値をsessionに保存し支払方法画面にGET通信
		session.setAttribute("orderForm", form);
		return "redirect:/client/order/payment/input";
	}

	//	支払方法選択画面の表示値のリクエストスコープへの登録
	/**
	 * @param model
	 * @return
	 */
	@RequestMapping(path = "/client/order/payment/input", method = RequestMethod.GET)
	public String paymentInput(Model model) {
		OrderForm orderForm = (OrderForm) session.getAttribute("orderForm");
		if (session.getAttribute("user") == null) {

			return "redirect:/login";
		}

		model.addAttribute("orderForm", orderForm);
		model.addAttribute("payMethod", orderForm.getPayMethod());
		return "client/order/payment_input";
	}

	//	入力された支払方法をセッション内の注文情報に登録し注文確認画面にGet通信
	/**
	 * @param form
	 * @return redirect:/client/order/check
	 */
	@RequestMapping(path = "/client/order/check", method = RequestMethod.POST)
	public String orderCheck(OrderForm form) {
		if (session.getAttribute("user") == null) {

			return "redirect:/login";
		}
		OrderForm orderForm = (OrderForm) session.getAttribute("orderForm");
		orderForm.setPayMethod(form.getPayMethod());
		orderForm.setCouponId(form.getCouponId());
		session.setAttribute("orderForm", orderForm);
		return "redirect:/client/order/check";
	}

	//	注文確認画面の表示と金額の計算
	/**
	 * @param model
	 * @return client/order/check
	 */
	@RequestMapping(path = "/client/order/check", method = RequestMethod.GET)
	public String orderCheck(Model model) {
		if (session.getAttribute("user") == null) {

			return "redirect:/login";
		}

		UserBean loginUser = (UserBean) session.getAttribute("user");
		User user = userRepository.getReferenceById(loginUser.getId());

		// 利用可能なクーポンリストを取得
		List<UserCoupon> coupons = userCouponRepository.findByUserAndIsUsedAndExpiryDateGreaterThanEqual(
				user, 0, Date.valueOf(LocalDate.now()));
		model.addAttribute("coupons", coupons);

		OrderForm orderForm = (OrderForm) session.getAttribute("orderForm");
		model.addAttribute("orderForm", orderForm);

		@SuppressWarnings("unchecked")
		List<BasketBean> basketList = (List<BasketBean>) session.getAttribute("basketBeans");

		List<String> itemNameListLessThan = new ArrayList<>();
		List<String> itemNameListZero = new ArrayList<>();

		//		買い物かご内の商品と最新のストック情報と照合し買い物かご内の商品を更新する
		for (BasketBean basketBean : basketList) {

			Item item = itemRepository.findById(basketBean.getId()).orElse(null);

			if (item == null || item.getStock() == 0) {

				itemNameListZero.add(basketBean.getName());

			} else if (item.getStock() < basketBean.getOrderNum()) {

				itemNameListLessThan.add(basketBean.getName());
				basketBean.setOrderNum(item.getStock());
			}
		}

		basketList.removeIf(basketBean -> {
			Item item = itemRepository.findById(basketBean.getId()).orElse(null);
			return item == null || item.getStock() == 0;
		});

		if (!itemNameListLessThan.isEmpty()) {
			model.addAttribute("itemNameListLessThan", itemNameListLessThan);
		}

		if (!itemNameListZero.isEmpty()) {
			model.addAttribute("itemNameListZero", itemNameListZero);
		}

		session.setAttribute("basketBeans", basketList);

		//		注文金額と商品画像の表示するためのマップ
		List<Map<String, Object>> orderItemBeans = new ArrayList<>();

		Integer sum = 0;

		//		各商品ごとの金額等合計と商品画像の登録
		for (BasketBean basketBean : basketList) {

			//			商品画像を取得するためのオブジェクト
			Item item = itemRepository.getReferenceById(basketBean.getId());

			Map<String, Object> orderItem = new HashMap<>();

			orderItem.put("name", item.getName());
			orderItem.put("image", item.getImage());
			orderItem.put("price", item.getPrice());
			orderItem.put("orderNum", basketBean.getOrderNum());

			Integer subtotal = item.getPrice() * basketBean.getOrderNum();

			orderItem.put("subtotal", subtotal);

			orderItemBeans.add(orderItem);

			sum += subtotal;
		}

		if (basketList.size() != 0) {
			model.addAttribute("orderItemBeans", orderItemBeans);
			model.addAttribute("total", sum);

			// 割引計算
			int discountRate = 0;
			if (orderForm.getCouponId() != null && orderForm.getCouponId() != 0) {
				// クーポンIDからクーポン情報を取得し、所有者チェックを行う
				UserCoupon coupon = userCouponRepository.findById(orderForm.getCouponId()).orElse(null);
				if (coupon != null && coupon.getIsUsed() == 0 && coupon.getUser().getId().equals(loginUser.getId())) {
					discountRate = coupon.getDiscountRate();
				}
			}

			int total = priceCalc.calculateDiscountedPrice(sum, discountRate);

			model.addAttribute("subtotalSum", sum);
			model.addAttribute("discountRate", discountRate);
			model.addAttribute("total", total);
		}

		return "client/order/check";
	}

	//	戻るボタンでアドレス届け先入力画面に戻るためのメソッド
	/**
	 * @return redirect:/client/order/address/input
	 */
	@RequestMapping(path = "/client/order/payment/back", method = RequestMethod.POST)
	public String orderBack() {
		if (session.getAttribute("user") == null) {

			return "redirect:/login";
		}
		return "redirect:/client/order/address/input";
	}

	//	注文内容の最終確認とtableへの登録更新と完了画面へのGet通信
	/**
	 * @param form
	 * @return redirect:/client/order/complete
	 */
	@RequestMapping(path = "/client/order/complete", method = RequestMethod.POST)
	public String orderComplete(OrderForm form) {

		OrderForm orderForm = (OrderForm) session.getAttribute("orderForm");
		if (session.getAttribute("user") == null) {

			return "redirect:/login";
		}

		//		ここでもう一度tableとの照合を行う
		@SuppressWarnings("unchecked")
		List<BasketBean> basketList = (List<BasketBean>) session.getAttribute("basketBeans");

		for (BasketBean basketBean : basketList) {
			Item item = itemRepository.findById(basketBean.getId()).orElse(null);
			if (item == null || item.getStock() < basketBean.getOrderNum()) {
				return "redirect:/client/order/check";
			}
		}

		//		オーダーtableへの登録を行う為にエンティティへの値の代入
		Order order = new Order();
		UserBean loginUser = (UserBean) session.getAttribute("user");
		User user = userRepository.getReferenceById(loginUser.getId());
		BeanUtils.copyProperties(orderForm, order, "id", "insertDate", "orderItemsList", "user");
		order.setInsertDate(Date.valueOf(LocalDate.now()));
		order.setUser(user);

		// クーポン情報の設定
		if (orderForm.getCouponId() != null && orderForm.getCouponId() != 0) {
			UserCoupon coupon = userCouponRepository.findById(orderForm.getCouponId()).orElse(null);
			if (coupon != null && coupon.getIsUsed() == 0 && coupon.getUser().getId().equals(user.getId())) {
				order.setDiscountRate(coupon.getDiscountRate());
				coupon.setIsUsed(1); // クーポンを使用済みにする
				userCouponRepository.save(coupon);
			}
		}

		order = orderRepository.save(order);

		//		注文詳細tableへの登録と商品の在庫の更新
		for (BasketBean basketBean : basketList) {
			Item item = itemRepository.getReferenceById(basketBean.getId());

			if (item.getIsSecret() != null && item.getIsSecret() == 1) {
				// ミステリーボックスの場合
				List<OrderItem> mysteryOrderItems = generateMysteryOrderItems(order, item, basketBean.getOrderNum());
				orderItemRepository.saveAll(mysteryOrderItems);
				// ミステリーボックスは在庫管理を行わない
			} else {
				// 通常商品の場合
				OrderItem orderItem = new OrderItem();
				orderItem.setOrder(order);
				orderItem.setItem(item);
				orderItem.setQuantity(basketBean.getOrderNum());
				orderItem.setPrice(item.getPrice());
				orderItemRepository.save(orderItem);

				item.setStock(item.getStock() - basketBean.getOrderNum());
				itemRepository.save(item);
			}
		}

		session.removeAttribute("basketBeans");
		session.removeAttribute("orderForm");

		return "redirect:/client/order/complete";
	}

	//	注文完了画面の表示
	/**
	 * @return client/order/complete
	 */
	@RequestMapping(path = "/client/order/complete", method = RequestMethod.GET)
	public String orderComplete() {
		if (session.getAttribute("user") == null) {

			return "redirect:/login";
		}
		return "client/order/complete";
	}

	/**
	 * 当選ランクの抽選
	 * @param grade グレード（商品価格）
	 * @return 当選ランク("S", "A", "B", "C")
	 */
	private String drawRank(final int grade) {
		List<MysteryItemWeight> weights = mysteryItemWeightRepository.findByGrade(grade);
		int totalWeight = weights.stream().mapToInt(MysteryItemWeight::getWeight).sum();
		int randomNum = ThreadLocalRandom.current().nextInt(totalWeight);

		int currentWeight = 0;
		for (MysteryItemWeight weight : weights) {
			currentWeight += weight.getWeight();
			if (randomNum < currentWeight) {
				return weight.getRank();
			}
		}
		return "C"; // フォールバック
	}

	/**
	 * ミステリーボックス注文明細の生成
	 * @param order 注文情報
	 * @param mysteryBoxItem ミステリーボックス商品
	 * @param quantity 注文個数
	 * @return 注文商品エンティティのリスト
	 */
	private List<OrderItem> generateMysteryOrderItems(final Order order, final Item mysteryBoxItem,
			final int quantity) {
		List<OrderItem> orderItems = new ArrayList<>();
		IntStream.range(0, quantity).forEach(i -> {
			String rank = drawRank(mysteryBoxItem.getPrice());
			List<MysteryItem> candidates = mysteryItemRepository.findByRankAndDeleteFlag(rank, Constant.NOT_DELETED);
			MysteryItem selected = candidates.get(ThreadLocalRandom.current().nextInt(candidates.size()));

			OrderItem orderItem = new OrderItem();
			orderItem.setOrder(order);
			orderItem.setItem(mysteryBoxItem);
			orderItem.setQuantity(1); // 1つずつ明細を作成し、それぞれに景品を紐づける
			orderItem.setIsMystery(1);
			orderItem.setMysteryItem(selected);
			orderItem.setPrice(mysteryBoxItem.getPrice());
			orderItems.add(orderItem);
		});
		return orderItems;
	}
}