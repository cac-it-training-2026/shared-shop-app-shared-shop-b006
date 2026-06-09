package jp.co.sss.shop.controller.client.item;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import jp.co.sss.shop.bean.ItemBean;
import jp.co.sss.shop.entity.Category;
import jp.co.sss.shop.entity.Item;
import jp.co.sss.shop.repository.CategoryRepository;
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
	 * カテゴリ情報リポジトリ
	 */
	@Autowired
	CategoryRepository categoryRepository;

	/**
	 * Entity、Form、Bean間のデータコピーサービス
	 */
	@Autowired
	BeanTools beanTools;

	/**
	 * サイドバー表示用の共通処理
	 * カテゴリリポジトリの既存メソッドに合わせて修正しました
	 */
	@ModelAttribute("categories")
	public List<Category> getCategories() {
		// ⭕ 既存の「登録日降順でリスト取得するメソッド」を呼び出す
		return categoryRepository.findByDeleteFlagOrderByInsertDateDescIdDesc(Constant.NOT_DELETED);
	}

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
		// 

		// 未削除の「売れ筋順」の商品リストを取得
		List<Item> itemList = itemRepository.findByDeleteFlagOrderBySalesDesc(Constant.NOT_DELETED);
		int sortType = 2; //初期値は売れ筋順("2")

		// もし売れ筋順の商品が1件もない場合は、バックアップとして「新着順」で取得
		if (itemList == null || itemList.isEmpty()) {
			itemList = itemRepository.findByDeleteFlagOrderByInsertDateDesc(Constant.NOT_DELETED);
			sortType = 1; // 新着順("1")に切り替え
		}

		// 3. トップ画面の表示仕様に合わせて「最大10件」にリストを切り出す
		if (itemList != null && itemList.size() > 10) {
			itemList = itemList.subList(0, 10);
		}

		// エンティティ内の検索結果をJavaBeansにコピー
		List<ItemBean> itemBeanList = beanTools.copyEntityListToItemBeanList(itemList);

		// 商品情報と「現在のソート順」Viewへ渡す
		model.addAttribute("items", itemBeanList);
		model.addAttribute("sortType", sortType);

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

		//プルダウンで「指定なし(0)」が選ばれた場合は、カテゴリ指定なし(null)に変換する
		if (categoryId != null && categoryId == 0) {
			categoryId = null;
		}

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