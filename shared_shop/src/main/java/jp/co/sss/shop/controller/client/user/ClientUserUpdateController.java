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
 * 一般会員の会員情報変更処理を制御するコントローラクラス
 */
@Controller
public class ClientUserUpdateController {

	/** 未削除状態を表す定数（削除フラグ: 0） */
	private static final int NOT_DELETED = 0;

	/** ユーザー情報にアクセスするためのリポジトリ */
	@Autowired
	private UserRepository userRepository;

	/** セッション */
	@Autowired
	private HttpSession session;

	/**
	 * 処理1：（変更ボタン 押下時処理）、（確認画面-戻るボタン 押下時処理）
	 * * @return 変更入力画面表示処理へのリダイレクトパス文字列
	 */
	@RequestMapping(path = "/client/user/update/input", method = RequestMethod.POST)
	public String updateInput() {
		UserForm userForm = (UserForm) session.getAttribute("userForm");
		if (userForm == null) {
			final UserBean loginUser = (UserBean) session.getAttribute("loginUser");
			if (loginUser == null) {
				return "redirect:/login";
			}

			final User user = userRepository.findByIdAndDeleteFlag(loginUser.getId(), NOT_DELETED);
			if (user == null) {
				return "redirect:/syserror";
			}

			userForm = new UserForm();

			BeanUtils.copyProperties(user, userForm);

			session.setAttribute("userForm", userForm);
		}

		return "redirect:/client/user/update/input";
	}

	/**
	 * 処理2：（変更入力画面表示処理）
	 * * @param model Viewとの値受渡し用のModelオブジェクト
	 * @return 変更入力画面の表示用テンプレート名（フォワード）
	 */
	@RequestMapping(path = "/client/user/update/input", method = RequestMethod.GET)
	public String updateInputView(final Model model) {
		final UserForm userForm = (UserForm) session.getAttribute("userForm");
		if (userForm == null) {
			return "redirect:/syserror";
		}
		model.addAttribute("userForm", userForm);

		final BindingResult result = (BindingResult) session.getAttribute("result");
		if (result != null) {
			model.addAttribute("org.springframework.validation.BindingResult.userForm", result);
			session.removeAttribute("result");
		}

		return "client/user/update_input";
	}

	/**
	 * 処理3：（確認ボタン 押下時処理）
	 * * @param userForm 画面から入力されたユーザー情報のフォームオブジェクト
	 * @param result 入力チェック結果
	 * @return 次の処理ステップに対応するリダイレクトパス文字列
	 */
	@RequestMapping(path = "/client/user/update/check", method = RequestMethod.POST)
	public String updateCheck(@Valid @ModelAttribute final UserForm userForm, final BindingResult result) {
		final UserForm lastUserForm = (UserForm) session.getAttribute("userForm");
		if (lastUserForm == null) {
			return "redirect:/syserror";
		}

		// 画面のhidden等から届いた権限を保持。万が一無ければ直前のセッションから補填
		if (userForm.getAuthority() == null) {
			userForm.setAuthority(lastUserForm.getAuthority());
		}

		session.setAttribute("userForm", userForm);

		if (result.hasErrors()) {
			session.setAttribute("result", result);
			return "redirect:/client/user/update/input";
		}

		return "redirect:/client/user/update/check";
	}

	/**
	 * 処理4：（変更確認画面表示処理）
	 * * @param model Viewとの値受渡し用のModelオブジェクト
	 * @return 変更確認画面の表示用テンプレート名（フォワード）
	 */
	@RequestMapping(path = "/client/user/update/check", method = RequestMethod.GET)
	public String updateCheckView(final Model model) {
		final UserForm userForm = (UserForm) session.getAttribute("userForm");
		if (userForm == null) {
			return "redirect:/syserror";
		}
		model.addAttribute("userForm", userForm);

		return "client/user/update_check";
	}

	/**
	 * 処理5：（登録ボタン 押下時処理）
	 * * @return 変更完了画面表示処理へのリダイレクトパス文字列
	 */
	@RequestMapping(path = "/client/user/update/complete", method = RequestMethod.POST)
	public String updateComplete() {
		final UserForm userForm = (UserForm) session.getAttribute("userForm");
		final UserBean loginUser = (UserBean) session.getAttribute("loginUser");
		if (userForm == null || loginUser == null) {
			return "redirect:/syserror";
		}

		final User user = userRepository.findByIdAndDeleteFlag(userForm.getId(), NOT_DELETED);
		if (user == null) {
			return "redirect:/syserror";
		}

		final Integer deleteFlag = user.getDeleteFlag();
		final java.sql.Date insertDate = user.getInsertDate();

		BeanUtils.copyProperties(userForm, user);

		user.setDeleteFlag(deleteFlag);
		user.setInsertDate(insertDate);

		userRepository.save(user);

		loginUser.setEmail(user.getEmail());
		loginUser.setName(user.getName());
		loginUser.setPostalCode(user.getPostalCode());
		loginUser.setAddress(user.getAddress());
		loginUser.setPhoneNumber(user.getPhoneNumber());
		session.setAttribute("loginUser", loginUser);

		session.removeAttribute("userForm");

		return "redirect:/client/user/update/complete";
	}

	/**
	 * 処理6：（変更完了画面表示処理）
	 * * @return 変更完了画面の表示用テンプレート名（フォワード）
	 */
	@RequestMapping(path = "/client/user/update/complete", method = RequestMethod.GET)
	public String updateCompleteView() {
		return "client/user/update_complete";
	}
}