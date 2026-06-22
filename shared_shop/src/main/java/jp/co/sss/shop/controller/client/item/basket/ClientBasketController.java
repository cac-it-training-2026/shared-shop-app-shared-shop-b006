package jp.co.sss.shop.controller.client.item.basket;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jakarta.servlet.http.HttpSession;
import jp.co.sss.shop.bean.BasketBean;
import jp.co.sss.shop.entity.Item;
import jp.co.sss.shop.form.LoginForm;
import jp.co.sss.shop.repository.ItemRepository;

/**
 * 買い物かごの処理を記述するコントローラー
 * @author 高戸
 */
@Controller
public class ClientBasketController {

	@Autowired
	HttpSession session;

	@Autowired
	ItemRepository itemRepository;

	//	買い物かごの中身の表示メソッド
	/**
	 * @param form
	 * @param model
	 * @return
	 */
	@RequestMapping(path = "/client/basket/list")
	String showBasket(@ModelAttribute LoginForm form, Model model) {

		if (session.getAttribute("user") == null) {
			return "redirect:/login";
		}

		@SuppressWarnings("unchecked")
		List<BasketBean> basketList = (List<BasketBean>) session.getAttribute("basketBeans");

		model.addAttribute(
				"itemNameListLessThan",
				session.getAttribute("itemNameListLessThan"));

		model.addAttribute(
				"itemNameListZero",
				session.getAttribute("itemNameListZero"));

		session.removeAttribute("itemNameListLessThan");
		session.removeAttribute("itemNameListZero");
		return "client/basket/list";
	}
	//	買い物かごへの追加と在庫数の確認を行うメソッド

	/**
	 * @param id
	 * @param quantity
	 * @param model
	 * @return
	 */
	@RequestMapping(path = "/client/basket/add", method = RequestMethod.POST)
	public String addBasket(final Integer id, final Integer quantity, final Model model) {

		final Integer orderNum = (quantity == null) ? 1 : quantity;

		@SuppressWarnings("unchecked")
		List<BasketBean> basketList = (List<BasketBean>) session.getAttribute("basketBeans");
		@SuppressWarnings("unchecked")
		List<String> itemNameListZero = (List<String>) session.getAttribute("itemNameListZero");

		if (itemNameListZero == null) {
			itemNameListZero = new ArrayList<>();
		}

		// セッションになければ生成
		if (basketList == null) {
			basketList = new ArrayList<>();
		}

		final Optional<BasketBean> existingBean = basketList.stream()
				.filter(b -> b.getId().equals(id))
				.findFirst();

		final Optional<Item> itemOpt = itemRepository.findById(id);

		if (itemOpt.isEmpty()) {
			return "redirect:/client/basket/list";
		}

		final Item item = itemOpt.get();

		// 同じ商品があるか確認
		if (existingBean.isPresent()) {
			final BasketBean basketBean = existingBean.get();

			// 最新の在庫数で更新
			basketBean.setStock(item.getStock());

			// 在庫切れ
			if (item.getStock() == 0) {
				if (!itemNameListZero.contains(item.getName())) {
					itemNameListZero.add(item.getName());
				}
				session.setAttribute("itemNameListZero", itemNameListZero);
			}
			// 在庫不足 (DBの最新在庫と比較)
			else if (item.getStock() < basketBean.getOrderNum() + orderNum) {

				@SuppressWarnings("unchecked")
				List<String> itemNameListLessThan = (List<String>) session.getAttribute(
						"itemNameListLessThan");

				if (itemNameListLessThan == null) {
					itemNameListLessThan = new ArrayList<>();
				}

				if (!itemNameListLessThan.contains(basketBean.getName())) {
					itemNameListLessThan.add(basketBean.getName());
				}

				session.setAttribute("itemNameListLessThan", itemNameListLessThan);

				// 在庫数分まで追加
				basketBean.setOrderNum(item.getStock());

			}
			// 在庫に余裕あり
			else {
				basketBean.setOrderNum(basketBean.getOrderNum() + orderNum);
			}
		} else {
			// なければ新規追加

			// 在庫切れ
			if (item.getStock() == 0) {

				if (!itemNameListZero.contains(item.getName())) {
					itemNameListZero.add(item.getName());
				}

				session.setAttribute("itemNameListZero", itemNameListZero);

				return "redirect:/client/basket/list";
			}

			// 在庫不足
			if (item.getStock() < orderNum) {
				@SuppressWarnings("unchecked")
				List<String> itemNameListLessThan = (List<String>) session.getAttribute(
						"itemNameListLessThan");

				if (itemNameListLessThan == null) {
					itemNameListLessThan = new ArrayList<>();
				}

				if (!itemNameListLessThan.contains(item.getName())) {
					itemNameListLessThan.add(item.getName());
				}

				session.setAttribute("itemNameListLessThan", itemNameListLessThan);
			}

			final BasketBean basketBean = new BasketBean();
			basketBean.setId(item.getId());
			basketBean.setName(item.getName());
			basketBean.setStock(item.getStock());
			basketBean.setOrderNum(Math.min(item.getStock(), orderNum));

			basketList.add(basketBean);
		}

		// セッションへ保存
		session.setAttribute("basketBeans", basketList);
		return "redirect:/client/basket/list";

	}

	/**
	 * 買い物かご内の商品の数量を直接更新するメソッド
	 * @param id
	 * @param quantity
	 * @param model
	 * @return
	 */
	@RequestMapping(path = "/client/basket/updateQuantity", method = RequestMethod.POST)
	public String updateQuantity(final Integer id, final Integer quantity, final Model model) {

		if (quantity == null || quantity <= 0) {
			return deleteBasket(id);
		}

		@SuppressWarnings("unchecked")
		List<BasketBean> basketList = (List<BasketBean>) session.getAttribute("basketBeans");

		if (basketList != null) {
			basketList.stream()
					.filter(b -> b.getId().equals(id))
					.findFirst()
					.ifPresent(basketBean -> {
						final Optional<Item> itemOpt = itemRepository.findById(id);
						if (itemOpt.isPresent()) {
							final Item item = itemOpt.get();
							// 最新の在庫数で更新
							basketBean.setStock(item.getStock());

							// 在庫切れ
							if (item.getStock() == 0) {
								@SuppressWarnings("unchecked")
								List<String> itemNameListZero = (List<String>) session.getAttribute("itemNameListZero");
								if (itemNameListZero == null) {
									itemNameListZero = new ArrayList<>();
								}
								if (!itemNameListZero.contains(item.getName())) {
									itemNameListZero.add(item.getName());
								}
								session.setAttribute("itemNameListZero", itemNameListZero);
								basketBean.setOrderNum(0);
							}
							// 在庫不足 (DBの最新在庫と比較)
							else if (item.getStock() < quantity) {
								@SuppressWarnings("unchecked")
								List<String> itemNameListLessThan = (List<String>) session.getAttribute("itemNameListLessThan");
								if (itemNameListLessThan == null) {
									itemNameListLessThan = new ArrayList<>();
								}
								if (!itemNameListLessThan.contains(basketBean.getName())) {
									itemNameListLessThan.add(basketBean.getName());
								}
								session.setAttribute("itemNameListLessThan", itemNameListLessThan);
								basketBean.setOrderNum(item.getStock());
							} else {
								basketBean.setOrderNum(quantity);
							}
						}
					});
		}

		return "redirect:/client/basket/list";
	}

	//	sessionに登録されているリストの中からIDを使用し該当商品の要素を削除する
	/**
	 * @param id
	 * @return
	 */
	@RequestMapping(path = "/client/basket/delete", method = RequestMethod.POST)
	public String deleteBasket(final Integer id) {
		@SuppressWarnings("unchecked")
		List<BasketBean> basketList = (List<BasketBean>) session.getAttribute("basketBeans");

		if (basketList != null) {
			basketList.removeIf(basketBean -> basketBean.getId().equals(id));
		}

		//		リストの中身が0の場合でもNullにはならないため買い物かごが空という表示を行うためsessionの削除を行う
		if (basketList == null || basketList.isEmpty()) {
			session.removeAttribute("basketBeans");
		} else {
			session.setAttribute("basketBeans", basketList);
		}
		return "redirect:/client/basket/list";

	}

	//	sessionの削除を行い買い物かごを空にする
	/**
	 * @return redirect:/client/basket/list
	 */
	@RequestMapping(path = "/client/basket/allDelete", method = RequestMethod.POST)
	String alldeleteBasket() {
		session.removeAttribute("basketBeans");
		return "redirect:/client/basket/list";
	}

}
