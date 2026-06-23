package jp.co.sss.shop.controller.client.item;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletRequest; // リンク元判定用
import jakarta.servlet.http.HttpSession; // セッション判定用
import jp.co.sss.shop.bean.ItemBean;
import jp.co.sss.shop.entity.Category;
import jp.co.sss.shop.entity.Item;
import jp.co.sss.shop.entity.Review;
import jp.co.sss.shop.repository.CategoryRepository;
import jp.co.sss.shop.repository.ItemRepository;
import jp.co.sss.shop.repository.ReviewRepository;
import jp.co.sss.shop.service.BeanTools;
import jp.co.sss.shop.util.Constant;

/**
 * 商品管理 一覧表示機能(一般会員用)のコントローラクラス
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
	 * レビュー情報 リポジトリ
	 */
	@Autowired
	ReviewRepository reviewRepository;

	//	/**
	//	 * サイドバー表示用の共通処理
	//	 */
	//	@ModelAttribute("categories")
	//	public List<Category> getCategories() {
	//		// 既存の「登録日降順でリスト取得するメソッド」を呼び出す
	//		return categoryRepository.findByDeleteFlagOrderByInsertDateDescIdDesc(Constant.NOT_DELETED);
	//	}

	/**
	 * トップ画面 表示処理
	 * 
	 * @author 近藤灯
	 * @param model    Viewとの値受渡し
	 * @return "index" トップ画面
	 */
	@RequestMapping(path = "/", method = { RequestMethod.GET, RequestMethod.POST })
	public String index(Model model, HttpSession session) {

		// 未削除の「売れ筋順」の商品リストを取得
		List<Item> salesList = itemRepository.findByDeleteFlagOrderBySalesDesc(Constant.NOT_DELETED);
		List<Item> latestList = itemRepository.findByDeleteFlagOrderByInsertDateDesc(Constant.NOT_DELETED);
		// 売れ筋商品用に「実際に購入された（注文数が1以上の）商品だけ」を入れるリストを用意
		List<Item> purchasedSalesList = new java.util.ArrayList<>();

		// 売れ筋リストを走査して、購入履歴がある商品だけをピックアップする
		if (salesList != null) {
			for (Item item : salesList) {
				// 注文商品リストが存在し、かつ1件以上データがある（＝購入されたことがある）場合のみ追加
				if (item.getOrderItemsList() != null && !item.getOrderItemsList().isEmpty()) {
					purchasedSalesList.add(item);
				}
			}
		}

		List<Item> itemList;
		int sortType = 2; // 初期値は売れ筋順(2)

		// ───【3パターン条件分岐】───

		// 判定A: 購入した商品がなく（絞り込みリストが空）、かつ登録されている商品情報も全くない場合
		if (purchasedSalesList.isEmpty() && (latestList == null || latestList.isEmpty())) {
			itemList = latestList; // 空のリスト（0件）をセット
			sortType = 2; // タイトルは「売れ筋商品」
		}
		// 判定B: 購入した商品はないが、登録されている商品情報だけはある場合（新着順に切り替え）
		else if (purchasedSalesList.isEmpty() && !latestList.isEmpty()) {
			itemList = latestList; // 新着順の商品リスト（全件）をセット
			sortType = 1; // タイトルは「新着商品」
		}
		// 判定C: 購入した商品がある場合（購入された商品だけをトップの売れ筋順に表示）
		else {
			itemList = purchasedSalesList; // 実際に購入された商品だけのリストをセット
			sortType = 2; // タイトルは「売れ筋商品」
		}

		// 3. トップ画面の表示仕様に合わせて「最大10件」にリストを切り出す
		if (itemList != null && itemList.size() > 10) {
			itemList = itemList.subList(0, 10);
		}

		// エンティティ内の検索結果をJavaBeansにコピー
		List<ItemBean> itemBeanList = beanTools.copyEntityListToItemBeanList(itemList);

		// N+1対策：ページ内に表示される商品のレビュー平均・件数を一括取得してBeanにセットする
		List<Integer> itemIds = itemBeanList.stream().map(ItemBean::getId).toList();
		if (!itemIds.isEmpty()) {
			List<Object[]> aggregates = reviewRepository.getReviewAggregatesByItemIds(itemIds, Constant.NOT_DELETED);
			for (ItemBean bean : itemBeanList) {
				Object[] agg = aggregates.stream().filter(a -> ((Integer) a[0]).equals(bean.getId())).findFirst().orElse(null);
				if (agg != null) {
					bean.setAverageRating(agg[1] != null ? ((Number) agg[1]).doubleValue() : 0.0);
					bean.setReviewCount(agg[2] != null ? ((Number) agg[2]).intValue() : 0);
				} else {
					bean.setAverageRating(0.0);
					bean.setReviewCount(0);
				}
			}
		}

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
	@RequestMapping(path = { "/client/item/list", "/client/item/list/{sortType}" }, method = { RequestMethod.GET,
			RequestMethod.POST })
	public String showItemList(
			@PathVariable(name = "sortType", required = false) Integer sortType,
			@RequestParam(name = "categoryId", required = false) Integer categoryId,
			Model model,
			HttpServletRequest request,
			HttpSession session) {

		// 初期表示および外部遷移の判定（画面のハードコーディング対策）
		String referer = request.getHeader("Referer");
		boolean isInitialEntry = (sortType == null) ||
				(sortType == 1 && (referer == null || !referer.contains("/client/item/list")));

		if (isInitialEntry) {
			// 全商材の注文履歴の有無を確認
			List<Item> allItems = itemRepository.findByDeleteFlagOrderBySalesDesc(Constant.NOT_DELETED);
			boolean hasOrderHistory = false;

			if (allItems != null) {
				for (Item item : allItems) {
					if (item.getOrderItemsList() != null && !item.getOrderItemsList().isEmpty()) {
						hasOrderHistory = true;
						break;
					}
				}
			}

			// 注文履歴がある場合は売れ筋順(2)、ない場合は新着順(1)に決定
			int targetSort = hasOrderHistory ? 2 : 1;

			// 指定されたソート順と異なる場合は自动リダイレクトを行う
			if (sortType == null || sortType != targetSort) {
				String redirectUrl = "redirect:/client/item/list/" + targetSort;
				if (categoryId != null) {
					redirectUrl += "?categoryId=" + categoryId;
				}
				return redirectUrl;
			}
		}

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

		// N+1対策：ページ内に表示される商品のレビュー平均・件数を一括取得してBeanにセットする
		List<Integer> itemIds2 = itemBeanList.stream().map(ItemBean::getId).toList();
		if (!itemIds2.isEmpty()) {
			List<Object[]> aggregates = reviewRepository.getReviewAggregatesByItemIds(itemIds2, Constant.NOT_DELETED);
			for (ItemBean bean : itemBeanList) {
				Object[] agg = aggregates.stream().filter(a -> ((Integer) a[0]).equals(bean.getId())).findFirst().orElse(null);
				if (agg != null) {
					bean.setAverageRating(agg[1] != null ? ((Number) agg[1]).doubleValue() : 0.0);
					bean.setReviewCount(agg[2] != null ? ((Number) agg[2]).intValue() : 0);
				} else {
					bean.setAverageRating(0.0);
					bean.setReviewCount(0);
				}
			}
		}

		// 画面に値を渡す
		model.addAttribute("items", itemBeanList);
		model.addAttribute("sortType", sortType);
		model.addAttribute("categoryId", categoryId);

		return "client/item/list";
	}

	/**
	 * 商品検索処理（あいまい検索）
	 * @param itemName 検索キーワード
	 * @param model Viewとの値受渡し
	 * @param session 検索履歴保存用セッション
	 * @return "client/item/list" 商品一覧画面
	 */
	@RequestMapping(path = "/client/item/search", method = { RequestMethod.GET, RequestMethod.POST })
	public String searchItem(
			@RequestParam(name = "itemName", required = false) String itemName,
			Model model,
			HttpSession session) {

		// 検索履歴の取得と保存
		@SuppressWarnings("unchecked")
		List<String> searchHistory = (List<String>) session.getAttribute("searchHistory");
		if (searchHistory == null) {
			searchHistory = new ArrayList<>();
		}

		if (itemName != null && !itemName.isBlank()) {
			// 重複を避けて先頭に追加（簡易的な履歴管理）
			searchHistory.remove(itemName);
			searchHistory.add(0, itemName);
			// 履歴は最大5件まで保持
			if (searchHistory.size() > 5) {
				searchHistory.remove(5);
			}
			session.setAttribute("searchHistory", searchHistory);
		}

		List<Item> itemList;
		if (itemName != null && !itemName.isBlank()) {
			itemList = itemRepository.findByNameContainingAndDeleteFlagOrderByInsertDateDesc(itemName,
					Constant.NOT_DELETED);
		} else {
			// キーワード空の場合は全件（新着順）
			itemList = itemRepository.findByDeleteFlagOrderByInsertDateDesc(Constant.NOT_DELETED);
		}

		// 検索結果をBeanリストに変換
		List<ItemBean> itemBeanList = beanTools.copyEntityListToItemBeanList(itemList);

		// 画面に値を渡す
		model.addAttribute("items", itemBeanList);
		model.addAttribute("sortType", 1); // 検索時は新着順ベースとして扱う
		model.addAttribute("itemName", itemName);

		return "client/item/list";
	}

	/**
	 * 詳細表示処理
	 */
	@RequestMapping(path = "/client/item/detail/{id}")
	public String showItem(@PathVariable int id, Model model, HttpSession session) {

		Item item = itemRepository.findByIdAndDeleteFlag(id, Constant.NOT_DELETED);
		if (item == null) {
			return "redirect:/syserror";
		}
		ItemBean itemBean = beanTools.copyEntityToItemBean(item);

		// レビュー一覧と集計情報を取得
		List<Review> reviewList = reviewRepository.findByItemIdAndDeleteFlagOrderByInsertDateDesc(id, Constant.NOT_DELETED);
		Double avgRating = reviewRepository.getAverageRating(id, Constant.NOT_DELETED);
		Long reviewCount = reviewRepository.getReviewCount(id, Constant.NOT_DELETED);
		itemBean.setAverageRating(avgRating != null ? avgRating : 0.0);
		itemBean.setReviewCount(reviewCount != null ? reviewCount.intValue() : 0);

		model.addAttribute("item", itemBean);
		model.addAttribute("reviews", reviewList);

		return "client/item/detail";
	}
}

/**
 * 全画面共通で使用するデータをModelへ設定するクラス。
 * サイドバーに表示するカテゴリ一覧を取得し、
 * 「categories」として全画面へ配信する。
 *
 * @author 近藤灯
 */

@org.springframework.web.bind.annotation.ControllerAdvice
class CommonControllerAdvice {

	@Autowired
	jp.co.sss.shop.repository.CategoryRepository categoryRepository;

	/**
	 * システム内のすべての画面にカテゴリリストを配信します
	 */
	@org.springframework.web.bind.annotation.ModelAttribute("categories")
	public List<Category> getCategories() {
		return categoryRepository.findByDeleteFlagOrderByInsertDateDescIdDesc(jp.co.sss.shop.util.Constant.NOT_DELETED);
	}
}