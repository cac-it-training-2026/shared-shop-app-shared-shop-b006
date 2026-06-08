package jp.co.sss.shop.controller.client.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

import jakarta.servlet.http.HttpSession;
import jp.co.sss.shop.bean.UserBean;
import jp.co.sss.shop.form.UserForm;
import jp.co.sss.shop.repository.UserRepository;

@Controller
public class ClientUserUpdateController {

	@Autowired
	private UserRepository userRepository;

	/**
	 * 処理1: 変更ボタン押下時処理 / 確認画面-戻るボタン押下時処理
	 * 一般会員変更は path = "/client/user/update/input", method = RequestMethod.POST
	 */

	@PostMapping("/client/user/update/input")
	public String updateInput(HttpSession session) {

		// セッションスコープに入力フォーム情報があるかを確認
		UserForm userForm = (UserForm) session.getAttribute("userForm");
		if (userForm == null) {
			// なければ、セッションに保存されたログインユーザーのIDを取得
			UserBean loginUser = (UserBean) session.getAttribute("user");
			if (loginUser == null) {
				return "redirect:/login";
			}
			// 3. ログインユーザーのIDを条件に変更対象のデータをDBから取得
			User user = userRepository.findById(loginUser.getId()).orElse(null);
			if (user == null) {
				return "common/error";
			}
		}
	}
}
