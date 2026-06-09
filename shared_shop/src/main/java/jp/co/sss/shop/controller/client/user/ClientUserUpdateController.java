package jp.co.sss.shop.controller.client.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jp.co.sss.shop.bean.UserBean;
import jp.co.sss.shop.entity.User; // ユーザー用Entity（UserEntity等の場合は要変更）
import jp.co.sss.shop.form.UserForm;
import jp.co.sss.shop.repository.UserRepository;

/**
 * 一般会員の会員情報変更処理を制御するコントローラクラスです。
 * * @author SharedShop制作チーム
 */
@Controller
public class ClientUserUpdateController {

	/** ユーザー情報にアクセスするためのリポジトリ */
	@Autowired
	private UserRepository userRepository;

	/**
	 * 変更入力画面への遷移、または確認画面から戻る際の初期化処理を行います。
	 * セッションスコープに入力フォーム情報がない場合、ログインユーザーの情報から初期フォームを生成します。
	 * * @param session セッションスコープにアクセスするためのHttpSessionオブジェクト
	 * @return 変更入力画面表示処理へのリダイレクトパス文字列
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
			// 取得データを元に入力画面初期表示用の入力フォーム情報を新規生成
			userForm = new UserForm();
			userForm.setEmail(user.getEmail());
			userForm.setName(user.getName());
			userForm.setPostalCode(user.getPostalCode());
			userForm.setAddress(user.getAddress());
			userForm.setPhoneNumber(user.getPhoneNumber());

			// 入力フォーム情報をセッションスコープに保存
			session.setAttribute("userForm", userForm);
		}

		// 変更入力画面表示処理へリダイレクト
		return "redirect:/client/user/update/input";
	}

	/**
	 * 変更入力画面を表示します。
	 * セッションからフォーム情報と入力エラー情報を取得し、リクエストスコープへ詰め替えます。
	 * * @param model リクエストスコープに値を設定するためのModelオブジェクト
	 * @param session セッションスコープから情報を取得するためのHttpSessionオブジェクト
	 * @return 変更入力画面の表示用テンプレート名
	 */
	@GetMapping("/client/user/update/input")
	public String updateInputView(Model model, HttpSession session) {
		// セッションスコープから入力フォーム情報を取得
		UserForm userForm = (session.getAttribute("userForm") != null)
				? (UserForm) session.getAttribute("userForm")
				: new UserForm();

		// 入力フォーム情報をリクエストスコープに設定
		model.addAttribute("userForm", userForm);

		// セッションスコープに入力エラー情報（BindingResultなど）がある場合
		if (session.getAttribute("org.springframework.validation.BindingResult.userForm") != null) {
			// 取得した入力エラー情報をリクエストスコープに設定
			model.addAttribute("org.springframework.validation.BindingResult.userForm",
					session.getAttribute("org.springframework.validation.BindingResult.userForm"));

			// セッションスコープから入力エラー情報を削除
			session.removeAttribute("org.springframework.validation.BindingResult.userForm");
		}

		// 変更入力画面表示（フォワード）
		return "client/user/update_input";
	}

	/**
	 * 入力された情報のバリデーション（入力チェック）を行います。
	 * 入力エラーが存在する場合は入力画面へ、問題ない場合は確認画面へとリダイレクトします。
	 * * @param userForm 画面から入力されたユーザー情報のフォームオブジェクト
	 * @param result 入力チェック結果を保持するBindingResultオブジェクト
	 * @param session セッションスコープにエラー情報を保存するためのHttpSessionオブジェクト
	 * @return 次の処理ステップに対応するリダイレクトパス文字列
	 */
	@PostMapping("/client/user/update/check")
	public String updateCheck(
			@Valid @ModelAttribute UserForm userForm,
			BindingResult result,
			HttpSession session) {

		// 画面から入力された入力フォームを、セッションスコープに入力フォーム情報として保存
		session.setAttribute("userForm", userForm);

		// BindingResultオブジェクトに入力エラー情報がある場合
		if (result.hasErrors()) {
			// 入力エラー情報をセッションスコープに設定
			session.setAttribute("org.springframework.validation.BindingResult.userForm", result);

			// 変更入力画面表示処理にリダイレクト
			return "redirect:/client/user/update/input";
		}

		// 入力エラーがない場合：変更確認画面表示処理にリダイレクト
		return "redirect:/client/user/update/check";
	}

	/**
	 * 変更内容の確認画面を表示します。
	 * セッションスコープから取得した入力情報を確認画面用テンプレートに引き渡します。
	 * * @param model リクエストスコープに値を設定するためのModelオブジェクト
	 * @param session セッションスコープから情報を取得するためのHttpSessionオブジェクト
	 * @return 変更確認画面の表示用テンプレート名
	 */
	@GetMapping("/client/user/update/check")
	public String updateCheckView(Model model, HttpSession session) {
		// セッションスコープから入力フォーム情報を取得
		UserForm userForm = (UserForm) session.getAttribute("userForm");
		if (userForm == null) {
			return "redirect:/client/user/update/input";
		}

		// 入力フォーム情報をリクエストスコープに設定
		model.addAttribute("userForm", userForm);

		// 登録確認画面表示（フォワード）
		return "client/user/update_check";
	}

	/**
	 * ユーザー情報の変更内容をデータベースに反映します。
	 * 更新処理完了後、現在のログインセッション情報も最新の状態へ同期します。
	 * * @param session セッションスコープのフォーム情報を取得し、ログイン情報を同期するためのHttpSessionオブジェクト
	 * @return 変更完了画面表示処理へのリダイレクトパス文字列
	 */
	@PostMapping("/client/user/update/complete")
	public String updateComplete(HttpSession session) {
		// セッションスコープから入力フォーム情報を取得
		UserForm userForm = (UserForm) session.getAttribute("userForm");
		UserBean loginUser = (UserBean) session.getAttribute("user");

		if (userForm == null || loginUser == null) {
			return "redirect:/client/user/update/input";
		}

		// 入力フォーム情報を元にDB登録用エンティティオブジェクトを生成（既存データを取得して上書き）
		User user = userRepository.findById(loginUser.getId()).orElse(null);
		if (user == null) {
			return "common/error";
		}

		user.setEmail(userForm.getEmail());
		user.setName(userForm.getName());
		user.setPostalCode(userForm.getPostalCode());
		user.setAddress(userForm.getAddress());
		user.setPhoneNumber(userForm.getPhoneNumber());

		// DB更新実施
		userRepository.save(user);

		// セッションスコープの入力フォーム情報削除
		session.removeAttribute("userForm");

		// 【ログインユーザの会員変更の場合、セッションスコープの会員情報を更新】
		loginUser.setEmail(user.getEmail());
		loginUser.setName(user.getName());
		loginUser.setPostalCode(user.getPostalCode());
		loginUser.setAddress(user.getAddress());
		loginUser.setPhoneNumber(user.getPhoneNumber());
		session.setAttribute("user", loginUser);

		// 変更完了画面表示処理にリダイレクト
		return "redirect:/client/user/update/complete";
	}

	/**
	 * 変更完了画面を表示します。
	 * * @return 変更完了画面の表示用テンプレート名
	 */
	@GetMapping("/client/user/update/complete")
	public String updateCompleteView() {
		// 登録完了画面表示（フォワード）
		return "client/user/update_complete";
	}
}
