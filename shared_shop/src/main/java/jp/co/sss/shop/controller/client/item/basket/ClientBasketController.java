package jp.co.sss.shop.controller.client.item.basket;

/**
 * 
 */

import java.util.ArrayList;
import java.util.List;

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
	 * @param orderNum
	 * @param model
	 * @return
	 */
	@RequestMapping(path = "/client/basket/add", method = RequestMethod.POST)
	public String addBasket(Integer id, Integer orderNum, Model model) {

		if (orderNum == null) {
			orderNum = 1;
		}

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

		boolean isExist = false;
		BasketBean basketBean = null;

		// 同じ商品があるか確認
		for (BasketBean itemInBasket : basketList) {

			if (itemInBasket.getId().equals(id)) {

				isExist = true;
				basketBean = itemInBasket;

				// DBから最新在庫を取得
				Item item = itemRepository.getReferenceById(id);

				// 在庫切れ
				if (item.getStock() == 0) {

					if (!itemNameListZero.contains(item.getName())) {
						itemNameListZero.add(item.getName());
					}

					session.setAttribute(
							"itemNameListZero",
							itemNameListZero);

				}
				// 在庫不足
				else if (item.getStock() < basketBean.getOrderNum() + orderNum) {

					@SuppressWarnings("unchecked")
					List<String> itemNameListLessThan = (List<String>) session.getAttribute(
							"itemNameListLessThan");

					if (itemNameListLessThan == null) {
						itemNameListLessThan = new ArrayList<>();
					}

					if (!itemNameListLessThan.contains(
							basketBean.getName())) {
						itemNameListLessThan.add(
								basketBean.getName());
					}

					session.setAttribute(
							"itemNameListLessThan",
							itemNameListLessThan);

				}
				// 在庫に余裕あり
				else {

					basketBean.setOrderNum(
							basketBean.getOrderNum() + orderNum);

				}

				break;
			}
		}

		// なければ新規追加
		if (!isExist) {

			Item item = itemRepository.findById(id).orElse(null);

			if (item == null) {
				return "redirect:/client/basket/list";
			}

			// 在庫切れ
			if (item.getStock() == 0) {

				if (!itemNameListZero.contains(item.getName())) {
					itemNameListZero.add(item.getName());
				}

				session.setAttribute(
						"itemNameListZero",
						itemNameListZero);

				return "redirect:/client/basket/list";
			}

			basketBean = new BasketBean();

			basketBean.setId(item.getId());
			basketBean.setName(item.getName());
			basketBean.setStock(item.getStock());

			basketBean.setOrderNum(orderNum);

			basketList.add(basketBean);
		}

		// セッションへ保存
		session.setAttribute("basketBeans", basketList);
		return "redirect:/client/basket/list";

	}

	//	買い物かご内の商品の数量を変更するメソッド
	/**
	 * @param id
	 * @param orderNum
	 * @param model
	 * @return
	 */
	@RequestMapping(path = "/client/basket/update", method = RequestMethod.POST)
	public String updateBasket(Integer id, Integer orderNum, Model model) {

		@SuppressWarnings("unchecked")
		List<BasketBean> basketList = (List<BasketBean>) session.getAttribute("basketBeans");

		if (basketList != null) {
			for (BasketBean basketBean : basketList) {
				if (basketBean.getId().equals(id)) {

					// DBから最新在庫を取得
					Item item = itemRepository.getReferenceById(id);

					// 在庫不足
					if (item.getStock() < orderNum) {

						@SuppressWarnings("unchecked")
						List<String> itemNameListLessThan = (List<String>) session.getAttribute(
								"itemNameListLessThan");

						if (itemNameListLessThan == null) {
							itemNameListLessThan = new ArrayList<>();
						}

						if (!itemNameListLessThan.contains(
								basketBean.getName())) {
							itemNameListLessThan.add(
									basketBean.getName());
						}

						session.setAttribute(
								"itemNameListLessThan",
								itemNameListLessThan);

					}
					// 在庫に余裕あり
					else {
						basketBean.setOrderNum(orderNum);
					}
					break;
				}
			}
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

		if (basketList != null) {
			basketList.removeIf(basketBean -> basketBean.getId().equals(id));
		}

		//		リストの中身が0の場合でもNullにはならないため買い物かごが空という表示を行うためsessionの削除を行う
		if (basketList == null || basketList.size() == 0) {
			session.removeAttribute("basketBeans");
			return "redirect:/client/basket/list";
		} else {
			session.setAttribute("basketBeans", basketList);
			return "redirect:/client/basket/list";
		}

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
