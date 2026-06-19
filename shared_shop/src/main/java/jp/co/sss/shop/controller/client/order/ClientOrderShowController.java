package jp.co.sss.shop.controller.client.order;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jakarta.servlet.http.HttpSession;
import jp.co.sss.shop.bean.OrderBean;
import jp.co.sss.shop.bean.OrderItemBean;
import jp.co.sss.shop.bean.UserBean;
import jp.co.sss.shop.entity.Order;
import jp.co.sss.shop.entity.OrderItem;
import jp.co.sss.shop.repository.OrderRepository;
import jp.co.sss.shop.repository.ReviewRepository;
import jp.co.sss.shop.service.BeanTools;
import jp.co.sss.shop.service.PriceCalc;

/**
 * 注文管理 一覧表示機能(一般会員用)のコントローラクラス
 * * 管理者用の処理をベースに、ログインユーザー自身の注文情報のみを取得
 */
@Controller
public class ClientOrderShowController {

	/**
	 * 注文情報 リポジトリ
	 */
	@Autowired
	private OrderRepository orderRepository;

	/**
	 * セッション
	 */
	@Autowired
	private HttpSession session;

	/**
	 * 合計金額計算
	 */
	@Autowired
	private PriceCalc priceCalc;

	/**
	 * Entity、Form、Bean間のデータ生成、コピー
	 */
	@Autowired
	private BeanTools beanTools;

	/**
	 * レビュー情報 リポジトリ
	 */
	@Autowired
	private ReviewRepository reviewRepository;

	/**
	 * 一覧取得、一覧画面表示 処理
	 *
	 * @param model Viewとの値受渡し
	 * @param pageable ページング情報
	 * @return "client/order/list" 注文情報 一覧画面へ
	 */
	@RequestMapping(path = "/client/order/list", method = { RequestMethod.GET, RequestMethod.POST })
	public String showOrderList(Model model, Pageable pageable) {

		// セッションスコープのレビュー投稿用フォーム関連属性を削除
		session.removeAttribute("reviewForm");
		session.removeAttribute("result");

		// セッションからログイン会員情報を取得
		final UserBean loginUser = (UserBean) session.getAttribute("user");
		if (loginUser == null) {
			// 未ログインの場合はログイン画面へリダイレクト
			return "redirect:/login";
		}

		// メソッドを呼び出し、現在のカート(NULL)を除外した「確定済みの注文履歴」のみを取得
		Page<Order> orderList = orderRepository
				.findByUserIdAndPayMethodIsNotNullOrderByInsertDateDescIdDesc(loginUser.getId(), pageable);

		// 注文情報リストを生成
		List<OrderBean> orderBeanList = new ArrayList<OrderBean>();
		for (Order order : orderList) {
			// BeanToolsクラスのcopyEntityToOrderBeanメソッドを使用して表示する注文情報を生成
			OrderBean orderBean = beanTools.copyEntityToOrderBean(order);
			// orderレコードから紐づくOrderItemのListを取り出す
			List<OrderItem> orderItemList = order.getOrderItemsList();
			// PriceCalcクラスのorderItemPriceTotalメソッドを使用して合計金額を算出
			int total = priceCalc.orderItemPriceTotal(orderItemList);

			// 合計金額のセット
			orderBean.setTotal(total);

			orderBeanList.add(orderBean);
		}

		// 注文情報リストをViewへ渡す
		model.addAttribute("pages", orderList);
		model.addAttribute("orders", orderBeanList);

		return "client/order/list";
	}

	/**
	 * 詳細表示処理
	 *
	 * @param id 詳細表示対象ID
	 * @param model Viewとの値受渡し
	 * @return "client/order/detail" 詳細画面 表示
	 */
	@RequestMapping(path = "/client/order/detail/{id}", method = RequestMethod.GET)
	public String showOrder(@PathVariable int id, Model model) {

		// セッションスコープのレビュー投稿用フォーム関連属性を削除
		session.removeAttribute("reviewForm");
		session.removeAttribute("result");

		// セッションからログイン会員情報を取得
		final UserBean loginUser = (UserBean) session.getAttribute("user");
		if (loginUser == null) {
			return "redirect:/login";
		}

		// 変更点：リポジトリのメソッドを使い、「注文ID」「ユーザーID」「支払方法が存在する（確定済）」の3条件で取得
		Optional<Order> orderOpt = orderRepository.findByIdAndUserIdAndPayMethodIsNotNull(id, loginUser.getId());

		// 変更点：データが存在しない（他人のデータ、カート状態、または存在しないID）場合は安全にシステムエラーへ飛ばす
		if (orderOpt.isEmpty()) {
			return "redirect:/syserror";
		}

		// Optionalからエンティティを取り出す
		Order order = orderOpt.get();

		// 表示する注文情報を生成
		OrderBean orderBean = beanTools.copyEntityToOrderBean(order);

		// 注文商品情報を取得
		List<OrderItemBean> orderItemBeanList = beanTools.generateOrderItemBeanList(order.getOrderItemsList());

		// レビュー投稿済みフラグを判定
		List<Boolean> reviewedList = order.getOrderItemsList().stream()
				.map(oi -> (Boolean) reviewRepository.existsByOrderIdAndItemIdAndDeleteFlag(order.getId(), oi.getItem().getId(), jp.co.sss.shop.util.Constant.NOT_DELETED))
				.toList();

		// 合計金額を算出
		int total = priceCalc.orderItemBeanPriceTotalUseSubtotal(orderItemBeanList);

		// 注文情報をViewへ渡す
		model.addAttribute("order", orderBean);
		model.addAttribute("orderItemBeans", orderItemBeanList);
		model.addAttribute("reviewedList", reviewedList);
		model.addAttribute("total", total);

		return "client/order/detail";
	}
}