package jp.co.sss.shop.controller.client.user;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jp.co.sss.shop.bean.UserBean;
import jp.co.sss.shop.entity.User;
import jp.co.sss.shop.form.UserForm;
import jp.co.sss.shop.repository.UserRepository;

/**
 * 会員管理 変更機能(一般会員)のコントローラクラス
 * 登録情報の多重送信を防止するため、Post-Redirect-Get（PRG）パターンを用いて実装
 * * 
 */
@Controller
public class ClientUserUpdateController {

	/** 未削除状態を表す定数（削除フラグ: 0） */
	private static final int NOT_DELETED = 0;

	/** 会員情報 リポジトリ */
	@Autowired
	private UserRepository userRepository;

	/** セッション */
	@Autowired
	private HttpSession session;

	/**
	 * 処理1：入力画面 初期表示処理(POST)
	 * セッションスコープより入力情報を取り出し、無ければログインユーザー情報から初期表示用フォーム情報を生成してセッションに保持
	 * * @return "redirect:/client/user/update/input" 変更入力画面 表示処理へのリダイレクト
	 */
	@RequestMapping(path = "/client/user/update/input", method = RequestMethod.POST)
	public String updateInput() {
		// セッションスコープより入力情報を取り出す
		UserForm userForm = (UserForm) session.getAttribute("userForm");
		if (userForm == null) {
			// セッションから一般会員のログイン情報を取得
			final UserBean loginUser = (UserBean) session.getAttribute("loginUser");
			if (loginUser == null) {
				// ログイン情報がない場合、ログイン画面へ
				return "redirect:/login";
			}

			// ログインユーザーのIDを元に、変更対象の情報取得
			final User user = userRepository.findByIdAndDeleteFlag(loginUser.getId(), NOT_DELETED);
			if (user == null) {
				// 対象が無い場合、エラー
				return "redirect:/syserror";
			}

			// 初期表示用フォーム情報の生成
			userForm = new UserForm();
			// 変更対象の情報をuserFormにコピー
			BeanUtils.copyProperties(user, userForm);

			// 変更入力フォームをセッションに保持
			session.setAttribute("userForm", userForm);
		}

		// 変更入力画面 表示処理へリダイレクト
		return "redirect:/client/user/update/input";
	}

	/**
	 * 処理2：入力画面 表示処理(GET)
	 * セッションから入力フォーム情報を取得して画面に設定し、セッションにエラー情報があればそれも画面へ設定して削除
	 * * @param model Viewとの値受渡し用のModelオブジェクト
	 * @return "client/user/update_input" 変更入力画面 表示（フォワード）
	 */
	@RequestMapping(path = "/client/user/update/input", method = RequestMethod.GET)
	public String updateInputView(final Model model) {
		// セッションから入力フォーム取得
		final UserForm userForm = (UserForm) session.getAttribute("userForm");
		if (userForm == null) {
			// セッション情報がない場合、エラー
			return "redirect:/syserror";
		}
		// 入力フォーム情報を画面表示設定
		model.addAttribute("userForm", userForm);

		final BindingResult result = (BindingResult) session.getAttribute("result");
		if (result != null) {
			// セッションにエラー情報がある場合、エラー情報を画面表示設定
			model.addAttribute("org.springframework.validation.BindingResult.userForm", result);
			// セッションのエラー情報を削除
			session.removeAttribute("result");
		}

		// 変更入力画面 表示（フォワード）
		return "client/user/update_input";
	}

	/**
	 * 処理3：変更確認処理(POST)
	 * 画面からの入力をセッションに保持し、入力値チェックの結果に応じて入力画面または確認画面へリダイレクト
	 * * @param form 入力フォームオブジェクト
	 * @param result 入力チェック結果
	 * @return 次の処理ステップに対応するリダイレクトパス文字列
	 */
	@RequestMapping(path = "/client/user/update/check", method = RequestMethod.POST)
	public String updateCheck(@Valid @ModelAttribute final UserForm form, final BindingResult result) {
		// 直前のセッション情報を取得
		final UserForm lastUserForm = (UserForm) session.getAttribute("userForm");
		if (lastUserForm == null) {
			// セッション情報が無い場合、エラー
			return "redirect:/syserror";
		}
		if (form.getAuthority() == null) {
			// 権限情報がない場合、セッション情報から値をセット
			form.setAuthority(lastUserForm.getAuthority());
		}

		// 入力フォーム情報をセッションに保持
		session.setAttribute("userForm", form);

		// 入力値にエラーがあった場合、入力画面に戻る
		if (result.hasErrors()) {
			session.setAttribute("result", result);
			// 変更入力画面 表示処理へリダイレクト
			return "redirect:/client/user/update/input";
		}

		// 変更確認画面 表示処理へリダイレクト
		return "redirect:/client/user/update/check";
	}

	/**
	 * 処理4：確認画面 表示処理(GET）
	 * セッションからフォーム情報を取得し、Modelに設定して変更確認画面を表示
	 * * @param model Viewとの値受渡し用のModelオブジェクト
	 * @return "client/user/update_check" 確認画面表示（フォワード）
	 */
	@RequestMapping(path = "/client/user/update/check", method = RequestMethod.GET)
	public String updateCheckView(final Model model) {
		// セッションから入力フォーム情報取得
		final UserForm userForm = (UserForm) session.getAttribute("userForm");
		if (userForm == null) {
			// セッション情報がない場合、エラー
			return "redirect:/syserror";
		}
		// 入力フォーム情報をスコープへ設定
		model.addAttribute("userForm", userForm);

		// 変更確認画面 表示（フォワード）
		return "client/user/update_check";
	}

	/**
	 * 処理5：変更登録処理(POST)
	 * DBの変更対象データを取得し、画面からの入力値で上書きして更新を行います。セッションのログインユーザー情報も最新情報に同期
	 * * @return "redirect:/client/user/update/complete" 変更完了画面 表示処理へのリダイレクト
	 */
	@RequestMapping(path = "/client/user/update/complete", method = RequestMethod.POST)
	public String updateComplete() {
		// セッション保持情報から入力値再取得
		final UserForm userForm = (UserForm) session.getAttribute("userForm");
		final UserBean loginUser = (UserBean) session.getAttribute("loginUser");
		if (userForm == null || loginUser == null) {
			// セッション情報がない場合、エラー
			return "redirect:/syserror";
		}

		// 変更対象情報を取得
		final User user = userRepository.findByIdAndDeleteFlag(userForm.getId(), NOT_DELETED);
		if (user == null) {
			// 対象が無い場合、エラー
			return "redirect:/syserror";
		}

		// 画面入力値以外の項目（削除フラグ、登録日）を退避
		final Integer deleteFlag = user.getDeleteFlag();
		final java.sql.Date insertDate = user.getInsertDate();

		// 入力フォーム情報を変更用エンティティに設定
		BeanUtils.copyProperties(userForm, user);

		// 入力値以外の項目を再設定
		user.setDeleteFlag(deleteFlag);
		user.setInsertDate(insertDate);

		// 情報を保存（UPDATE実行）
		userRepository.save(user);

		// ログインユーザ情報変更のため、セッション保存ユーザ情報（loginUser）をすべて最新に更新
		loginUser.setEmail(user.getEmail());
		loginUser.setName(user.getName());
		loginUser.setPostalCode(user.getPostalCode());
		loginUser.setAddress(user.getAddress());
		loginUser.setPhoneNumber(user.getPhoneNumber());
		session.setAttribute("loginUser", loginUser);

		// 不要になった入力フォームセッション情報の削除
		session.removeAttribute("userForm");

		// 変更完了画面 表示処理へリダイレクト（二重送信防止）
		return "redirect:/client/user/update/complete";
	}

	/**
	 * 処理6：変更完了画面 表示処理(GET)
	 * * @return "client/user/update_complete" 変更完了画面 表示（フォワード）
	 */
	@RequestMapping(path = "/client/user/update/complete", method = RequestMethod.GET)
	public String updateCompleteView() {
		// 変更完了画面 表示
		return "client/user/update_complete";
	}
}