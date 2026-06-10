package jp.co.sss.shop.controller.client.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jakarta.servlet.http.HttpSession;
import jp.co.sss.shop.bean.UserBean;
import jp.co.sss.shop.entity.User;
import jp.co.sss.shop.repository.UserRepository;
import jp.co.sss.shop.util.Constant;

/**
 * 会員管理 削除機能(一般会員用)のコントローラクラス
 * * @author SystemShared
 */
@Controller
public class ClientUserDeleteController {

	/** 会員情報リポジトリ */
	@Autowired
	UserRepository userRepository;

	/**
	 * 削除確認画面 表示処理
	 * * @param model   Viewとの値受渡し用オブジェクト
	 * @param session ログイン状態を管理するセッション
	 * @return "client/user/delete_check" 削除確認画面のHTML名
	 */
	@RequestMapping(path = "/client/user/delete/check", method = { RequestMethod.GET, RequestMethod.POST })
	public String deletecheck(Model model, HttpSession session) {

		// セッションからログイン中の会員情報を取得
		UserBean userBean = (UserBean) session.getAttribute("user");
		
		// サーバー再起動等でセッションが空の場合、エラー画面へリダイレクト
		if (userBean == null) {
			return "redirect:/syserror";
		}

		
		// 画面側（delete_check.html）での変数名の不一致によるNullPointerエラー（EL1007E）を防ぐため、
		// 可能性のある属性名「user」「userBean」「userForm」のすべてにデータを詰め込んでViewに渡す
		model.addAttribute("user", userBean);
		model.addAttribute("userBean", userBean); 
		model.addAttribute("userForm", userBean); 

		// 削除確認画面を直接リターン
		return "client/user/delete_check";
	}

	/**
	 * 削除処理、完了画面 表示処理
	 * * @param session ログイン状態を管理するセッション
	 * @return "client/user/delete_complete" 削除完了画面のHTML名
	 */
	@RequestMapping(path = "/client/user/delete/complete", method = { RequestMethod.GET, RequestMethod.POST })
	public String deleteComplete(HttpSession session) {

		// セッションからログイン中の会員情報を取得
		UserBean userBean = (UserBean) session.getAttribute("user");
		if (userBean == null) {
			return "redirect:/syserror";
		}

		// ログイン中の会員IDを条件に、該当する未削除の会員情報を取得
		User user = userRepository.findByIdAndDeleteFlag(userBean.getId(), Constant.NOT_DELETED);
		if (user == null) {
			return "redirect:/syserror";
		}

		// 削除フラグを1にする（論理削除）
		user.setDeleteFlag(Constant.DELETED);
		userRepository.save(user);

		// 退会手続きが完了したため、セッションを無効化（キャッシュクリア）
		session.invalidate();

		// 削除完了画面を直接リターン
		return "client/user/delete_complete";
	}
}