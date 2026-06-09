//package jp.co.sss.shop.controller.client.user;
//
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//
//import jakarta.servlet.http.HttpSession;
//import jp.co.sss.shop.bean.UserBean;
//import jp.co.sss.shop.entity.User;
//import jp.co.sss.shop.repository.UserRepository;
//import jp.co.sss.shop.util.Constant;
//
///**
// * 会員管理 削除功能(一般会員用)のコントローラクラス
// */
//@Controller
//public class ClientUserDeleteController {
//
//	/** 会員情報 リポジトリ */
//	@Autowired
//	UserRepository userRepository;
//
//	/** セッション */
//	@Autowired
//	HttpSession session;
//
//	/**
//	 * No.22: 削除確認画面 表示処理(POST)
//	 * * @param model Viewとの値受渡し
//	 * @return "client/user/delete_check" 削除確認画面
//	 */
//	@RequestMapping(path = "/client/user/delete/check", method = RequestMethod.POST)
//	public String deletecheck(Model model, HttpSession session) {
//
//		// セッションからログイン中の会員情報を取得
//		UserBean userBean = (UserBean) session.getAttribute("user");
//		if (userBean == null) {
//			// 異常系：エラー画面へリダイレクト
//			return "redirect:/syserror";
//		}
//
//		// 画面表示用にログイン会員情報を設定
//		model.addAttribute("user", userBean);
//
//		// 削除確認画面を直接リターン
//		return "client/user/delete_check";
//	}
//
//	/**
//	 * No.23: 削除処理、完了画面 表示処理(POST)8
//	 * * @return "client/user/delete_complete" 会員情報削除完了画面
//	 */
//	@RequestMapping(path = "/client/user/delete/complete", method = RequestMethod.POST)
//	public String deleteComplete(HttpSession session) {
//
//		// セッションからログイン中の会員情報を取得
//		UserBean userBean = (UserBean) session.getAttribute("user");
//		if (userBean == null) {
//			return "redirect:/syserror";
//		}
//
//		// ログイン中の会員IDを条件に、該当する会員情報を取得
//		User user = userRepository.findByIdAndDeleteFlag(userBean.getId(), Constant.NOT_DELETED);
//		if (user == null) {
//			return "redirect:/syserror";
//		}
//
//		// 削除フラグを1にする（論理削除）
//		user.setDeleteFlag(Constant.DELETED);
//		userRepository.save(user);
//
//		// 退会が完了したため、セッションを無効化
//		session.invalidate();
//
//		// 削除完了画面を直接リターン
//		return "client/user/delete_complete";
//	}
//}a