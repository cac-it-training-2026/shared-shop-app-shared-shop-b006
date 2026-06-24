package jp.co.sss.shop.controller.client;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpSession;
import jp.co.sss.shop.bean.UserBean;
import jp.co.sss.shop.entity.User;
import jp.co.sss.shop.entity.UserCoupon;
import jp.co.sss.shop.repository.UserCouponRepository;
import jp.co.sss.shop.repository.UserRepository;
import jp.co.sss.shop.service.RouletteService;

/**
 * ルーレット機能用コントローラクラス
 *
 * @author Jules
 */
@Controller
public class RouletteController {

	@Autowired
	UserRepository userRepository;

	@Autowired
	UserCouponRepository userCouponRepository;

	@Autowired
	RouletteService rouletteService;

	@Autowired
	HttpSession session;

	/**
	 * ルーレット画面表示
	 * @return "client/roulette/roulette"
	 */
	@RequestMapping(path = "/client/roulette", method = RequestMethod.GET)
	public String showRoulette() {
		UserBean loginUser = (UserBean) session.getAttribute("user");
		if (loginUser == null) {
			return "redirect:/login";
		}
		return "client/roulette/roulette";
	}

	/**
	 * ルーレット実行と結果登録 (AJAX用)
	 * @return ルーレット結果(JSON)
	 */
	@RequestMapping(path = "/client/roulette/run", method = RequestMethod.POST)
	@ResponseBody
	public RouletteService.RouletteResult runRoulette() {
		UserBean loginUser = (UserBean) session.getAttribute("user");
		if (loginUser == null) {
			return null;
		}

		User user = userRepository.getReferenceById(loginUser.getId());
		Date today = Date.valueOf(LocalDate.now());

		// 二重実行防止
		if (user.getLastRouletteDate() != null && user.getLastRouletteDate().equals(today)) {
			return null;
		}

		// ルーレット実行
		RouletteService.RouletteResult result = rouletteService.determineRouletteResult();

		// ユーザーの最終実行日を更新
		user.setLastRouletteDate(today);
		userRepository.save(user);

		// クーポンを保存
		UserCoupon coupon = new UserCoupon();
		coupon.setUser(user);
		coupon.setDiscountRate(result.getDiscountRate());
		coupon.setIsUsed(0); // 未使用
		// 有効期限は1週間後とする
		coupon.setExpiryDate(Date.valueOf(LocalDate.now().plusWeeks(1)));
		userCouponRepository.save(coupon);

		return result;
	}

	/**
	 * ルーレット結果画面表示
	 * @param model
	 * @return "client/roulette/result"
	 */
	@RequestMapping(path = "/client/roulette/result", method = RequestMethod.GET)
	public String showResult(Model model) {
		UserBean loginUser = (UserBean) session.getAttribute("user");
		if (loginUser == null) {
			return "redirect:/login";
		}

		User user = userRepository.getReferenceById(loginUser.getId());
		Date today = Date.valueOf(LocalDate.now());

		// 今日のクーポンを取得
		List<UserCoupon> coupons = userCouponRepository.findByUserAndIsUsedAndExpiryDateGreaterThanEqual(
				user, 0, today);

		if (coupons.isEmpty()) {
			return "redirect:/client/roulette";
		}

		// IDの降順でソート（最新のクーポンを確実に取得）
		coupons.sort((c1, c2) -> c2.getId().compareTo(c1.getId()));
		UserCoupon latestCoupon = coupons.get(0);
		model.addAttribute("discountRate", latestCoupon.getDiscountRate());

		return "client/roulette/result";
	}
}
