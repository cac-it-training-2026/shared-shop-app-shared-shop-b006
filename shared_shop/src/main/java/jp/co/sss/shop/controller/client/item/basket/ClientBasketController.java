package jp.co.sss.shop.controller.client.item.basket;

/**
 * 
 */

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

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
	public String addBasket(Integer id, @RequestParam(defaultValue = "1") final Integer quantity, Model model) {

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

		Optional<BasketBean> basketBeanOpt = basketList.stream().filter(b -> b.getId().equals(id)).findFirst();

		// 同じ商品があるか確認
		if (basketBeanOpt.isPresent()) {
			BasketBean basketBean = basketBeanOpt.get();

			// 加算後の注文数が追加時点の在庫（スナップショット）を超えるか確認
			if (basketBean.getStock() < basketBean.getOrderNum() + quantity) {

				@SuppressWarnings("unchecked")
				List<String> itemNameListLessThan = (List<String>) session.getAttribute("itemNameListLessThan");

				if (itemNameListLessThan == null) {
					itemNameListLessThan = new ArrayList<>();
				}

				if (!itemNameListLessThan.contains(basketBean.getName())) {
					itemNameListLessThan.add(basketBean.getName());
				}

				session.setAttribute("itemNameListLessThan", itemNameListLessThan);

			}
			// 在庫に余裕あり
			else {
				basketBean.setOrderNum(basketBean.getOrderNum() + quantity);
			}
		}
		// なければ新規追加
		else {
			Item item = itemRepository.findById(id).orElse(null);

			if (item == null) {
				return "redirect:/client/basket/list";
			}

			// 在庫切れ/不足チェック
			if (item.getStock() == 0) {
				if (!itemNameListZero.contains(item.getName())) {
					itemNameListZero.add(item.getName());
				}
				session.setAttribute("itemNameListZero", itemNameListZero);
				return "redirect:/client/basket/list";
			} else if (item.getStock() < quantity) {
				@SuppressWarnings("unchecked")
				List<String> itemNameListLessThan = (List<String>) session.getAttribute("itemNameListLessThan");
				if (itemNameListLessThan == null) {
					itemNameListLessThan = new ArrayList<>();
				}
				if (!itemNameListLessThan.contains(item.getName())) {
					itemNameListLessThan.add(item.getName());
				}
				session.setAttribute("itemNameListLessThan", itemNameListLessThan);
				return "redirect:/client/basket/list";
			}

			BasketBean basketBean = new BasketBean();
			basketBean.setId(item.getId());
			basketBean.setName(item.getName());
			basketBean.setStock(item.getStock());
			basketBean.setOrderNum(quantity);

			basketList.add(basketBean);
		}

		// セッションへ保存
		session.setAttribute("basketBeans", basketList);
		return "redirect:/client/basket/list";

	}

	/**
	 * 買い物かご内の商品の数量を直接更新するメソッド
	 * @param id 商品ID
	 * @param quantity 更新後の数量
	 * @return 買い物かご画面へのリダイレクト
	 */
	@RequestMapping(path = "/client/basket/updateQuantity", method = RequestMethod.POST)
	public String updateQuantity(Integer id, final Integer quantity) {
		@SuppressWarnings("unchecked")
		List<BasketBean> basketList = (List<BasketBean>) session.getAttribute("basketBeans");

		if (basketList == null) {
			return "redirect:/client/basket/list";
		}

		if (quantity <= 0) {
			// 数量0以下の場合は即座に削除
			basketList.removeIf(basketBean -> basketBean.getId().equals(id));
		} else {
			Optional<BasketBean> basketBeanOpt = basketList.stream().filter(b -> b.getId().equals(id)).findFirst();

			if (basketBeanOpt.isPresent()) {
				BasketBean basketBean = basketBeanOpt.get();

				// 追加時点の在庫（スナップショット）と比較
				if (basketBean.getStock() < quantity) {
					@SuppressWarnings("unchecked")
					List<String> itemNameListLessThan = (List<String>) session.getAttribute("itemNameListLessThan");

					if (itemNameListLessThan == null) {
						itemNameListLessThan = new ArrayList<>();
					}

					if (!itemNameListLessThan.contains(basketBean.getName())) {
						itemNameListLessThan.add(basketBean.getName());
					}

					session.setAttribute("itemNameListLessThan", itemNameListLessThan);
				} else {
					basketBean.setOrderNum(quantity);
				}
			}
		}

		if (basketList.isEmpty()) {
			session.removeAttribute("basketBeans");
		} else {
			session.setAttribute("basketBeans", basketList);
		}
		return "redirect:/client/basket/list";
	}

	//	sessionに登録されているリストの中からIDを使用し該当商品の要素を削除する
	/**
	 * @param id
	 * @return
	 */
	@RequestMapping(path = "/client/basket/delete", method = RequestMethod.POST)
	String deleteBasket(Integer id) {
		@SuppressWarnings("unchecked")
		List<BasketBean> basketList = (List<BasketBean>) session.getAttribute("basketBeans");

		if (basketList == null) {
			return "redirect:/client/basket/list";
		}

		// 削除ボタン押下時は-1する既存仕様を維持
		for (BasketBean basket : basketList) {
			if (basket.getId().equals(id)) {
				if (basket.getOrderNum() >= 1) {
					basket.setOrderNum(basket.getOrderNum() - 1);
				}
			}
		}
		basketList.removeIf(basketBean -> basketBean.getId().equals(id) && basketBean.getOrderNum() < 1);

		//		リストの中身が0の場合でもNullにはならないため買い物かごが空という表示を行うためsessionの削除を行う
		if (basketList.size() == 0) {
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
