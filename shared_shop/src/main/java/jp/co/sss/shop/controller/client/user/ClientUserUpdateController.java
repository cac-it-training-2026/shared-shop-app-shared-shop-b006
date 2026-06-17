package jp.co.sss.shop.controller.client.user;

import java.sql.Date;

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
import jp.co.sss.shop.util.Constant;

/**
 * 会員管理 変更機能(一般会員用)のコントローラクラス
 * * 
 * @author:日野遥矢
 * 
 */

@Controller
public class ClientUserUpdateController {

	/**
	 * 会員情報 リポジトリ
	 */
	@Autowired
	UserRepository userRepository;

	/**
	 * セッション
	 */
	@Autowired
	HttpSession session;

	/**
	 * 入力画面 初期表示処理(POST)
	 * * 一般会員は自分自身の変更なので、URLの{id}は不要（セッションのuserから自分のIDを特定）
	 * @return "redirect:/client/user/update/input" 入力画面 表示処理
	 */
	@RequestMapping(path = "/client/user/update/input", method = RequestMethod.POST)
	public String updateInput() {

		// 【共通】セッション切れ（未ログイン）チェック
		UserBean loginUser = (UserBean) session.getAttribute("user");
		if (loginUser == null) {
			return "redirect:/login";
		}

		// セッションスコープより入力情報を取り出す
		UserForm userForm = (UserForm) session.getAttribute("userForm");
		if (userForm == null) {

			// 変更対象（自分自身）の情報取得
			User user = userRepository.findByIdAndDeleteFlag(loginUser.getId(), Constant.NOT_DELETED);
			if (user == null) {
				return "redirect:/syserror";
			}

			// 初期表示用フォーム情報の生成
			userForm = new UserForm();
			BeanUtils.copyProperties(user, userForm);

			// 変更入力フォームをセッションに保持
			session.setAttribute("userForm", userForm);
		}

		return "redirect:/client/user/update/input";
	}

	/**
	 * 入力画面 表示処理
	 *
	 * @param model Viewとの値受渡し
	 * @return "client/user/update_input" 変更入力画面 表示
	 */
	@RequestMapping(path = "/client/user/update/input", method = RequestMethod.GET)
	public String updateInputView(Model model) {

		if (session.getAttribute("user") == null) {
			return "redirect:/login";
		}

		UserForm userForm = (UserForm) session.getAttribute("userForm");
		if (userForm == null) {
			return "redirect:/syserror";
		}

		model.addAttribute("userForm", userForm);

		BindingResult result = (BindingResult) session.getAttribute("result");
		if (result != null) {
			model.addAttribute("org.springframework.validation.BindingResult.userForm", result);
			session.removeAttribute("result");
		}

		return "client/user/update_input";
	}

	/**
	 * 変更確認処理
	 * @param form   入力された会員情報のフォームオブジェクト
	 * @param result 入力チェックの検証結果
	 * 
	 */
	@RequestMapping(path = "/client/user/update/check", method = RequestMethod.POST)
	public String updateCheck(@Valid @ModelAttribute UserForm form, BindingResult result) {

		if (session.getAttribute("user") == null) {
			return "redirect:/login";
		}

		UserForm lastUserForm = (UserForm) session.getAttribute("userForm");
		if (lastUserForm == null) {
			return "redirect:/syserror";
		}

		// 権限や会員IDがない場合、セッション情報から値をセット（不足の補填）
		if (form.getAuthority() == null) {
			form.setAuthority(lastUserForm.getAuthority());
		}
		if (form.getId() == null) {
			form.setId(lastUserForm.getId());
		}

		session.setAttribute("userForm", form);

		if (result.hasErrors()) {
			session.setAttribute("result", result);
			return "redirect:/client/user/update/input";
		}

		return "redirect:/client/user/update/check";
	}

	/**
	 * 確認画面 表示処理
	 * @param model Viewとの値受渡し用のModelオブジェクト
	 * @return "client/user/update_check" 変更確認画面
	 */
	@RequestMapping(path = "/client/user/update/check", method = RequestMethod.GET)
	public String updateCheckView(Model model) {
		if (session.getAttribute("user") == null) {
			return "redirect:/login";
		}

		UserForm userForm = (UserForm) session.getAttribute("userForm");
		if (userForm == null) {
			return "redirect:/syserror";
		}
		model.addAttribute("userForm", userForm);

		return "client/user/update_check";
	}

	/**
	 * 変更登録、完了画面表示処理
	 * @return "redirect:/client/user/update/complete" 変更完了画面
	 */
	@RequestMapping(path = "/client/user/update/complete", method = RequestMethod.POST)
	public String updateComplete() {

		// 【共通】セッション切れ（未ログイン）チェック
		UserBean loginUser = (UserBean) session.getAttribute("user");
		if (loginUser == null) {
			return "redirect:/login";
		}

		UserForm userForm = (UserForm) session.getAttribute("userForm");
		if (userForm == null) {
			return "redirect:/syserror";
		}

		User user = userRepository.findByIdAndDeleteFlag(userForm.getId(), Constant.NOT_DELETED);
		if (user == null) {
			return "redirect:/syserror";
		}

		Integer deleteFlag = user.getDeleteFlag();
		Date insertDate = user.getInsertDate();

		BeanUtils.copyProperties(userForm, user);

		user.setDeleteFlag(deleteFlag);
		user.setInsertDate(insertDate);

		userRepository.save(user);

		// キー名「user」内のログインセッション情報を最新状態に同期する処理
		if (loginUser.getId().equals(userForm.getId())) {
			BeanUtils.copyProperties(user, loginUser);
		}
		session.setAttribute("user", loginUser);

		session.removeAttribute("userForm");

		return "redirect:/client/user/update/complete";
	}

	/**
	 * 変更完了画面 表示
	 * @return "client/user/update_complete" 変更完了画面
	 */
	@RequestMapping(path = "/client/user/update/complete", method = RequestMethod.GET)
	public String updateCompleteView() {

		if (session.getAttribute("user") == null) {
			return "redirect:/login";
		}
		return "client/user/update_complete";
	}
}