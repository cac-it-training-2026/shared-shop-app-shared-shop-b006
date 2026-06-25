package jp.co.sss.shop.controller.client.item.review;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;
import jp.co.sss.shop.bean.UserBean;
import jp.co.sss.shop.entity.Review;
import jp.co.sss.shop.entity.ReviewHelpfulLog;
import jp.co.sss.shop.entity.User;
import jp.co.sss.shop.repository.ReviewHelpfulLogRepository;
import jp.co.sss.shop.repository.ReviewRepository;

/**
 * レビューの「参考になった」ボタン用コントローラクラス
 *
 * @author Jules
 */
@Controller
public class ClientReviewHelpfulController {

	/**
	 * レビュー情報 リポジトリ
	 */
	@Autowired
	private ReviewRepository reviewRepository;

	/**
	 * 「参考になった」ログ リポジトリ
	 */
	@Autowired
	private ReviewHelpfulLogRepository reviewHelpfulLogRepository;

	/**
	 * セッション
	 */
	@Autowired
	private HttpSession session;

	/**
	 * 「参考になった」ボタン押下処理
	 *
	 * @param reviewId レビューID
	 * @return 商品詳細画面へリダイレクト
	 */
	@PostMapping("/client/review/helpful")
	public String handleHelpful(@RequestParam Integer reviewId) {
		// セッションからログイン会員情報を取得
		final UserBean loginUser = (UserBean) session.getAttribute("user");
		if (loginUser == null) {
			return "redirect:/login";
		}

		// レビューの存在確認
		Optional<Review> reviewOpt = reviewRepository.findById(reviewId);
		if (reviewOpt.isEmpty()) {
			return "redirect:/syserror";
		}

		Review review = reviewOpt.get();

		// レビュー投稿者本人の場合は処理をスキップ
		if (loginUser.getId().equals(review.getUser().getId())) {
			return "redirect:/client/item/detail/" + review.getItem().getId();
		}

		// 重複判定（既に「参考になった」を押しているか）
		if (!reviewHelpfulLogRepository.existsByReviewIdAndUserId(reviewId, loginUser.getId())) {
			// 未投票の場合のみ保存
			ReviewHelpfulLog log = new ReviewHelpfulLog();
			log.setReview(review);

			User user = new User();
			user.setId(loginUser.getId());
			log.setUser(user);

			reviewHelpfulLogRepository.save(log);

			// 対象レビューのhelpfulCountを1増やす
			reviewRepository.incrementHelpfulCount(reviewId);

		}

		// 商品詳細画面へリダイレクト
		return "redirect:/client/item/detail/" + review.getItem().getId();
	}
}
