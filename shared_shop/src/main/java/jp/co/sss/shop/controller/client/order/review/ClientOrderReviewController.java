package jp.co.sss.shop.controller.client.order.review;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jp.co.sss.shop.bean.UserBean;
import jp.co.sss.shop.entity.Order;
import jp.co.sss.shop.entity.Review;
import jp.co.sss.shop.form.ReviewForm;
import jp.co.sss.shop.repository.ItemRepository;
import jp.co.sss.shop.repository.OrderRepository;
import jp.co.sss.shop.repository.ReviewRepository;
import jp.co.sss.shop.repository.UserRepository;

/**
 * レビュー投稿機能のコントローラクラス
 *
 * @author Jules
 */
@Controller
public class ClientOrderReviewController {

	/**
	 * 注文情報 リポジトリ
	 */
	@Autowired
	private OrderRepository orderRepository;

	/**
	 * レビュー情報 リポジトリ
	 */
	@Autowired
	private ReviewRepository reviewRepository;

	/**
	 * 商品情報 リポジトリ
	 */
	@Autowired
	private ItemRepository itemRepository;

	/**
	 * 会員情報 リポジトリ
	 */
	@Autowired
	private UserRepository userRepository;

	/**
	 * セッション
	 */
	@Autowired
	private HttpSession session;

	/**
	 * 処理1（投稿開始）: レビュー投稿の初期化処理
	 *
	 * @param orderId 注文ID
	 * @param itemId 商品ID
	 * @return 入力画面表示処理へリダイレクト
	 */
	@GetMapping("/client/order/review/regist/input/init")
	public String registInputInit(@RequestParam Integer orderId, @RequestParam Integer itemId) {
		// セッションからログイン会員情報を取得
		final UserBean loginUser = (UserBean) session.getAttribute("user");
		if (loginUser == null) {
			return "redirect:/login";
		}

		// 購入済み・確定済みであることの検証
		// NOTE: Use standard findById and verify conditions to avoid JPA method parsing issues that trigger syserror.
		Optional<Order> orderOpt = orderRepository.findById(orderId);
		if (orderOpt.isEmpty() || !orderOpt.get().getUser().getId().equals(loginUser.getId()) || orderOpt.get().getPayMethod() == null) {
			return "redirect:/syserror";
		}

		Order order = orderOpt.get();
		// 対象の商品が含まれているか検証
		boolean containsItem = order.getOrderItemsList().stream()
				.anyMatch(oi -> oi.getItem().getId().equals(itemId));
		if (!containsItem) {
			return "redirect:/syserror";
		}

		// 既に投稿済みでないことを検証
		if (reviewRepository.existsByOrderIdAndItemIdAndDeleteFlag(orderId, itemId, jp.co.sss.shop.util.Constant.NOT_DELETED)) {
			return "redirect:/client/order/detail/" + orderId;
		}

		// ReviewFormを新規生成しセッションスコープに保存
		ReviewForm reviewForm = new ReviewForm();
		reviewForm.setOrderId(orderId);
		reviewForm.setItemId(itemId);
		reviewForm.setDisplayName(loginUser.getName()); // デフォルトは会員登録名
		session.setAttribute("reviewForm", reviewForm);

		return "redirect:/client/order/review/regist/input";
	}

	/**
	 * 処理2（入力画面表示）: レビュー投稿入力画面を表示
	 *
	 * @param model Viewとの値受渡し
	 * @return "client/order/review/regist_input" レビュー投稿入力画面
	 */
	@GetMapping("/client/order/review/regist/input")
	public String registInputView(Model model) {
		ReviewForm reviewForm = (ReviewForm) session.getAttribute("reviewForm");
		if (reviewForm == null) {
			return "redirect:/client/order/list";
		}

		model.addAttribute("reviewForm", reviewForm);

		// 入力エラー情報があればセッションから取り出して設定・削除
		BindingResult result = (BindingResult) session.getAttribute("result");
		if (result != null) {
			model.addAttribute("org.springframework.validation.BindingResult.reviewForm", result);
			session.removeAttribute("result");
		}

		return "client/order/review/regist_input";
	}

	/**
	 * 処理3（確認ボタン押下）: 入力内容のバリデーションと確認画面への遷移
	 *
	 * @param reviewForm レビュー投稿フォーム
	 * @param result バリデーション結果
	 * @return エラーがあれば入力画面へ、なければ確認画面表示処理へリダイレクト
	 */
	@PostMapping("/client/order/review/regist/check")
	public String registCheck(@Valid @ModelAttribute ReviewForm reviewForm, BindingResult result) {
		ReviewForm sessionForm = (ReviewForm) session.getAttribute("reviewForm");
		if (sessionForm == null) {
			return "redirect:/client/order/list";
		}

		// 画面入力値をセッションのReviewFormに反映
		if (reviewForm.getIsAnonymous() != null && reviewForm.getIsAnonymous()) {
			// メッセージプロパティを使いたいところだが、固定文字列で匿名ユーザーをセットするか、
			// HTML側で#messagesを利用させる。ここではFormのdisplayNameを書き換える
			sessionForm.setDisplayName("匿名ユーザー");
			sessionForm.setIsAnonymous(true);
		} else {
			sessionForm.setDisplayName(reviewForm.getDisplayName());
			sessionForm.setIsAnonymous(false);
		}
		sessionForm.setTitle(reviewForm.getTitle());
		sessionForm.setBody(reviewForm.getBody());
		sessionForm.setRating(reviewForm.getRating());
		session.setAttribute("reviewForm", sessionForm);

		if (result.hasErrors()) {
			session.setAttribute("result", result);
			return "redirect:/client/order/review/regist/input";
		}

		return "redirect:/client/order/review/regist/check";
	}

	/**
	 * 処理4（確認画面表示）: レビュー投稿確認画面を表示
	 *
	 * @param model Viewとの値受渡し
	 * @return "client/order/review/regist_check" レビュー投稿確認画面
	 */
	@GetMapping("/client/order/review/regist/check")
	public String registCheckView(Model model) {
		ReviewForm reviewForm = (ReviewForm) session.getAttribute("reviewForm");
		if (reviewForm == null) {
			return "redirect:/client/order/list";
		}

		model.addAttribute("reviewForm", reviewForm);
		return "client/order/review/regist_check";
	}

	/**
	 * 処理5（登録ボタン押下）: レビュー情報の登録
	 *
	 * @return 完了画面表示処理へリダイレクト
	 */
	@PostMapping("/client/order/review/regist/complete")
	public String registComplete() {
		ReviewForm reviewForm = (ReviewForm) session.getAttribute("reviewForm");
		if (reviewForm == null) {
			return "redirect:/client/order/list";
		}

		// セッションからログイン会員情報を取得
		final UserBean loginUser = (UserBean) session.getAttribute("user");
		if (loginUser == null) {
			return "redirect:/login";
		}

		// Reviewエンティティを生成・保存
		Review review = new Review();
		review.setOrder(orderRepository.getReferenceById(reviewForm.getOrderId()));
		review.setItem(itemRepository.getReferenceById(reviewForm.getItemId()));
		review.setUser(userRepository.getReferenceById(loginUser.getId()));

		review.setDisplayName(reviewForm.getDisplayName());
		review.setTitle(reviewForm.getTitle());
		review.setBody(reviewForm.getBody());
		review.setRating(reviewForm.getRating());
		review.setHelpfulCount(0);

		reviewRepository.save(review);

		// セッションのReviewFormを削除
		session.removeAttribute("reviewForm");

		return "redirect:/client/order/review/regist/complete";
	}

	/**
	 * 処理6（完了画面表示）: レビュー投稿完了画面を表示
	 *
	 * @return "client/order/review/regist_complete" レビュー投稿完了画面
	 */
	@GetMapping("/client/order/review/regist/complete")
	public String registCompleteView() {
		return "client/order/review/regist_complete";
	}
}
