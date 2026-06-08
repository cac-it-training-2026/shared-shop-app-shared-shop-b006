package jp.co.sss.shop.controller.client.item;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import jp.co.sss.shop.bean.ItemBean;
import jp.co.sss.shop.entity.Item;
import jp.co.sss.shop.repository.ItemRepository;
import jp.co.sss.shop.service.BeanTools;
import jp.co.sss.shop.util.Constant;

/**
 * 商品管理 一覧表示機能(一般会員用)のコントローラクラス
 *
 * @author SystemShared
 */
@Controller
public class ClientItemShowController {
	/**
	 * 商品情報
	 */
	@Autowired
	ItemRepository itemRepository;

	/**
	 * Entity、Form、Bean間のデータコピーサービス
	 */
	@Autowired
	BeanTools beanTools;

	/**
	 * トップ画面 表示処理
	 *
	 * @param model    Viewとの値受渡し
	 * @return "index" トップ画面
	 */
	@RequestMapping(path = "/", method = { RequestMethod.GET, RequestMethod.POST })
	public String index(Model model) {

		/*TODO 現在は全件表示を行っている
		 * これを売れ筋（注文回数が多い順）に改修する*/
		// 💡ここは「近藤さん」の担当なので、元の状態のまま触らずに残しておきます！

		// 注文情報の商品情報を全件表示
		List<Item> itemList = itemRepository.findAll();

		// エンティティ内の検索結果をJavaBeansにコピー
		List<ItemBean> itemBeanList = beanTools.copyEntityListToItemBeanList(itemList);

		// 商品情報をViewへ渡す
		model.addAttribute("items", itemBeanList);

		return "index";
	}

	/**
	 * 商品一覧画面 表示処理（新着順・売れ筋順）
	 * @param sortType 1:新着順, 2:売れ筋順
	 * @param categoryId カテゴリID（任意）
	 * @param model Viewとの値受渡し
	 * @return "client/item/list" 商品一覧画面
	 */
	@RequestMapping(path = "/client/item/list/{sortType}", method = { RequestMethod.GET, RequestMethod.POST })
	public String showItemList(@PathVariable int sortType,
			@RequestParam(name = "categoryId", required = false) Integer categoryId, Model model) {

		List<Item> itemList;

		// 1. 売れ筋順 (sortType == 2) の場合
		if (sortType == 2) {
			if (categoryId != null) {
				itemList = itemRepository.findByCategoryIdAndDeleteFlagOrderBySalesDesc(categoryId,
						Constant.NOT_DELETED);
			} else {
				itemList = itemRepository.findByDeleteFlagOrderBySalesDesc(Constant.NOT_DELETED);
			}
		}
		// 2. 新着順 (sortType == 1) の場合
		else {
			if (categoryId != null) {
				itemList = itemRepository.findByCategoryIdAndDeleteFlagOrderByInsertDateDesc(categoryId,
						Constant.NOT_DELETED);
			} else {
				itemList = itemRepository.findByDeleteFlagOrderByInsertDateDesc(Constant.NOT_DELETED);
			}
		}

		// 検索結果をBeanリストに変換
		List<ItemBean> itemBeanList = beanTools.copyEntityListToItemBeanList(itemList);

		// 画面に値を渡す
		model.addAttribute("items", itemBeanList);
		model.addAttribute("sortType", sortType);
		model.addAttribute("categoryId", categoryId);

		return "client/item/list";
	}

	/**
	 * 詳細表示処理
	 */
	@RequestMapping(path = "/client/item/detail/{id}")
	public String showItem(@PathVariable int id, Model model) {
		Item item = itemRepository.findByIdAndDeleteFlag(id, Constant.NOT_DELETED);
		if (item == null) {
			return "redirect:/syserror";
		}
		ItemBean itemBean = beanTools.copyEntityToItemBean(item);
		model.addAttribute("item", itemBean);
		return "client/item/detail";
	}
}