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
import jp.co.sss.shop.entity.User;
import jp.co.sss.shop.form.UserForm;
import jp.co.sss.shop.repository.UserRepository;

/**
 * 一般会員の会員情報変更処理を制御するコントローラクラス
 * * 
 */
@Controller
public class ClientUserUpdateController {

	/** 未削除状態を表す定数（削除フラグ: 0） */
	private static final int NOT_DELETED = 0;

	/** ユーザー情報にアクセスするためのリポジトリ */
	@Autowired
	private UserRepository userRepository;

	/**
	 * 処理1：（変更ボタン 押下時処理）、（確認画面-戻るボタン 押下時処理）を行います。
	 * セッションスコープに入力フォーム情報があるかを確認し、なければセッションに保存されたIDを使用して変更対象データを取得し、初期表示用のフォーム情報を新規生成してセッションに保存します。その後、変更入力画面表示処理へリダイレクトします。
	 * * @param session セッションスコープにアクセスするためのHttpSessionオブジェクト
	 * @return 変更入力画面表示処理へのリダイレクトパス文字列
	 */
	@PostMapping("/client/user/update/input")
	public String updateInput(final HttpSession session) {
		// セッションスコープに入力フォーム情報があるかを確認
		final UserForm userForm = (UserForm) session.getAttribute("userForm");

		if (userForm == null) {
			// なければ下記の処理を実施
			// 一般会員変更はセッションに保存されたID（loginUser）を使用し、変更対象のデータをDBから取得
			final UserBean loginUser = (UserBean) session.getAttribute("loginUser");
			if (loginUser == null) {
				return "redirect:/login";
			}

			final User user = userRepository.findByIdAndDeleteFlag(loginUser.getId(), NOT_DELETED);
			if (user == null) {
				return "common/error";
			}

			// 取得データを元に入力画面初期表示用の入力フォーム情報を新規生成
			final UserForm initialForm = new UserForm();
			initialForm.setId(user.getId());
			initialForm.setEmail(user.getEmail());
			initialForm.setPassword(user.getPassword());
			initialForm.setName(user.getName());
			initialForm.setPostalCode(user.getPostalCode());
			initialForm.setAddress(user.getAddress());
			initialForm.setPhoneNumber(user.getPhoneNumber());
			initialForm.setAuthority(user.getAuthority());

			// 入力フォーム情報をセッションスコープに保存
			session.setAttribute("userForm", initialForm);
		}

		// 変更入力画面表示処理へリダイレクト
		return "redirect:/client/user/update/input";
	}

	/**
	 * 処理2：（変更入力画面表示処理）を行います。
	 * セッションスコープから入力フォーム情報を取得してリクエストスコープに設定します。セッションスコープに入力エラー情報がある場合はそれをリクエストスコープに設定し、セッションから削除した上で、変更入力画面を表示します。
	 * * @param model リクエストスコープに値を設定するためのModelオブジェクト
	 * @param session セッションスコープから情報を取得するためのHttpSessionオブジェクト
	 * @return 変更入力画面の表示用テンプレート名（フォワード）
	 */
	@GetMapping("/client/user/update/input")
	public String updateInputView(final Model model, final HttpSession session) {
		// セッションスコープから入力フォーム情報を取得
		final Object sessionForm = session.getAttribute("userForm");
		final UserForm userForm = (sessionForm != null) ? (UserForm) sessionForm : new UserForm();

		// 入力フォーム情報をリクエストスコープに設定
		model.addAttribute("userForm", userForm);

		// セッションスコープに入力エラー情報がある場合
		final String errorKey = "org.springframework.validation.BindingResult.userForm";
		final Object bindingResult = session.getAttribute(errorKey);
		if (bindingResult != null) {
			// 取得した入力エラー情報をリクエストスコープに設定
			model.addAttribute(errorKey, bindingResult);

			// セッションスコープから、入力エラー情報を削除
			session.removeAttribute(errorKey);
		}

		// 変更入力画面表示（フォワード）
		return "client/user/update_input";
	}

	/**
	 * 処理3：（確認ボタン 押下時処理）を行います。
	 * 画面から入力された入力フォームをセッションスコープに入力フォーム情報として保存し、BindingResultに入力エラー情報がある場合はセッションに設定して変更入力画面表示処理にリダイレクトします。エラーがない場合は変更確認画面表示処理にリダイレクトします。
	 * * @param userForm 画面から入力されたユーザー情報のフォームオブジェクト
	 * @param result 入力チェック結果を保持するBindingResultオブジェクト
	 * @param session セッションスコープにフォーム情報やエラー情報を保存するためのHttpSessionオブジェクト
	 * @return 次の処理ステップに対応するリダイレクトパス文字列
	 */
	@PostMapping("/client/user/update/check")
	public String updateCheck(
			@Valid @ModelAttribute final UserForm userForm,
			final BindingResult result,
			final HttpSession session) {

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
	 * 処理4：（変更確認画面表示処理）を行います。
	 * セッションスコープから入力フォーム情報を取得し、リクエストスコープに設定した上で登録確認画面を表示します。
	 * * @param model リクエストスコープに値を設定するためのModelオブジェクト
	 * @param session セッションスコープから情報を取得するためのHttpSessionオブジェクト
	 * @return 変更確認画面の表示用テンプレート名（フォワード）
	 */
	@GetMapping("/client/user/update/check")
	public String updateCheckView(final Model model, final HttpSession session) {
		// セッションスコープから入力フォーム情報を取得
		final UserForm userForm = (UserForm) session.getAttribute("userForm");
		if (userForm == null) {
			return "redirect:/client/user/update/input";
		}

		// 入力フォーム情報をリクエストスコープに設定
		model.addAttribute("userForm", userForm);

		// 登録確認画面表示（フォワード）
		return "client/user/update_check";
	}

	/**
	 * 処理5：（登録ボタン 押下時処理）を行います。
	 * セッションスコープから入力フォーム情報を取得してDB登録用エンティティオブジェクトを生成し、DB更新を実施します。その後、セッションスコープの入力フォーム情報を削除し、ログインユーザの会員情報を更新した上で変更完了画面表示処理にリダイレクトします。
	 * * @param session セッションスコープのフォーム情報を取得し、会員情報を同期するためのHttpSessionオブジェクト
	 * @return 変更完了画面表示処理へのリダイレクトパス文字列
	 */
	@PostMapping("/client/user/update/complete")
	public String updateComplete(final HttpSession session) {
		// セッションスコープから入力フォーム情報を取得
		final UserForm userForm = (UserForm) session.getAttribute("userForm");
		final UserBean loginUser = (UserBean) session.getAttribute("loginUser");

		if (userForm == null || loginUser == null) {
			return "redirect:/client/user/update/input";
		}

		// 入力フォーム情報を元にDB登録用エンティティオブジェクトを生成（既存データを取得して上書き）
		final User user = userRepository.findByIdAndDeleteFlag(loginUser.getId(), NOT_DELETED);
		if (user == null) {
			return "common/error";
		}

		user.setEmail(userForm.getEmail());
		user.setPassword(userForm.getPassword());
		user.setName(userForm.getName());
		user.setPostalCode(userForm.getPostalCode());
		user.setAddress(userForm.getAddress());
		user.setPhoneNumber(userForm.getPhoneNumber());

		// DB更新実施
		userRepository.save(user);

		// セッションスコープの入力フォーム情報削除
		session.removeAttribute("userForm");

		// ログインユーザの会員変更の場合、セッションスコープの会員情報を更新
		loginUser.setEmail(user.getEmail());
		loginUser.setName(user.getName());
		loginUser.setPostalCode(user.getPostalCode());
		loginUser.setAddress(user.getAddress());
		loginUser.setPhoneNumber(user.getPhoneNumber());
		session.setAttribute("loginUser", loginUser);

		// 変更完了画面表示処理にリダイレクト
		return "redirect:/client/user/update/complete";
	}

	/**
	 * 処理6：（変更完了画面表示処理）を行います。
	 * 登録完了画面を表示します。
	 * * @return 変更完了画面の表示用テンプレート名（フォワード）
	 */
	@GetMapping("/client/user/update/complete")
	public String updateCompleteView() {
		// 登録完了画面表示（フォワード）
		return "client/user/update_complete";
	}
}