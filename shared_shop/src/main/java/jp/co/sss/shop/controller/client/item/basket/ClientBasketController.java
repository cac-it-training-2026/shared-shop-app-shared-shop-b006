package jp.co.sss.shop.controller.client.item.basket;

/**
 * 
 */

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jakarta.servlet.http.HttpSession;
import jp.co.sss.shop.bean.BasketBean;
import jp.co.sss.shop.entity.Item;
import jp.co.sss.shop.repository.ItemRepository;

/**
 * 買い物かごの処理のうち削除以外の処理を記述するコントローラー
 */

@Controller
public class ClientBasketController {

	@Autowired
	HttpSession session;

	@Autowired
	ItemRepository itemRepository;

	//	買い物かごの中身の表示メソッド
	@RequestMapping(path = "/client/basket/list")
	String showBasket() {

		return "client/basket/list";
	}

	//	買い物かごへの追加と在庫数の確認を行うメソッド

	@RequestMapping(path = "/client/basket/add", method = RequestMethod.POST)
	public String addBasket(Integer id, Model model) {

		@SuppressWarnings("unchecked")
		List<BasketBean> basketList = (List<BasketBean>) session.getAttribute("basketBeans");

		// セッションになければ生成
		if (basketList == null) {
			basketList = new ArrayList<>();
		}

		boolean isExist = false;
		BasketBean basketBean = null;

		// 同じ商品があるか確認
		for (BasketBean itemInBasket : basketList) {

			if (itemInBasket.getId().equals(id)) {

				basketBean = itemInBasket;
				//				stockが0または注文に対してstockが足りない場合に在庫がないという表示をフロントで行うためのリクエストスコープへの登録
				if (basketBean.getStock() == 0) {
					model.addAttribute("itemNameListZero", basketBean.getName());
				} else if (basketBean.getStock() < basketBean.getOrderNum() + 1) {
					model.addAttribute("itemNameListLessThan", basketBean.getName());
				} else {
					// 注文数を1増やす
					basketBean.setOrderNum(
							basketBean.getOrderNum() + 1);

					isExist = true;
				}
				break;
			}
		}

		// なければ新規追加
		if (!isExist) {

			Item item = itemRepository.getReferenceById(id);
			if (item.getStock() == 0) {
				model.addAttribute("itemNameListZero", item.getName());
			} else {
				basketBean = new BasketBean();

				basketBean.setId(item.getId());
				basketBean.setName(item.getName());
				basketBean.setStock(item.getStock());

				basketBean.setOrderNum(1);

				basketList.add(basketBean);

			}

		}

		// セッションへ保存
		session.setAttribute("basketBeans", basketList);

		return "redirect:/client/basket/list";
	}

	//	sessionに登録されているリストの中からIDを使用し該当商品の要素を削除する
	@RequestMapping(path = "/client/basket/delete", method = RequestMethod.POST)
	String deleteBasket(Integer id) {
		@SuppressWarnings("unchecked")
		List<BasketBean> basketList = (List<BasketBean>) session.getAttribute("basketBeans");
		for (int i = 0; i < basketList.size(); i++) {

			if (basketList.get(i).getId().equals(id)) {
				basketList.remove(i);
				break;
			}
		}
		//		リストの中身が0の場合でもNullにはならないため買い物かごが空という表示を行うためsessionの削除を行う
		if (basketList.size() == 0) {
			session.removeAttribute("basketBeans");
			return "redirect:/client/basket/list";
		}
		session.setAttribute("basketBeans", basketList);
		return "redirect:/client/basket/list";
	}

	//	sessionの削除を行い買い物かごを空にする
	@RequestMapping(path = "/client/basket/allDelete", method = RequestMethod.POST)
	String alldeleteBasket() {
		session.removeAttribute("basketBeans");
		return "redirect:/client/basket/list";
	}

}
