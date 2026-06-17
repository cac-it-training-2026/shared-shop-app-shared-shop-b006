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
 * 
 */
@Controller
public class ClientUserRegistController {
	@Autowired
	UserRepository userRepository;
	@Autowired
	HttpSession session;

	/**
	 * 処理１：会員登録画面の初期化を行います。
	 * 一般会員権限を設定した上で、会員情報入力用フォームを生成。
	 * セッションスコープに保存し、入力画面へリダイレクト
	 * @author :加藤拓海
	 * @param:session　セッション情報
	 * @return:会員登録入力画面へのリダイレクト先
	 */
	@GetMapping("/client/user/regist/input/init")
	public String registInputInit(final HttpSession session) {
		//入力フォーム情報を新規生成
		UserForm userForm = new UserForm();
		//生成したフォーム情報に一般会員権限（２）を渡す
		userForm.setAuthority(2);
		//生成したフォーム情報をセッションスコープに渡す
		session.setAttribute("userForm", userForm);
		return "redirect:/client/user/regist/input";
	}

	/**
	 * 処理２：会員登録画面への遷移処理を行う。（input/initとは入口が違う）
	 * セッション内に入力フォーム情報が存在しない場合は
	 * 新規作成して一般会員権限を設定し、入力画面へリダイレクト
	 * @author :加藤拓海
	 * @param session　セッション情報
	 * @return　会員登録入力画面へのリダイレクト先
	 */
	@PostMapping(path = "/client/user/regist/input")
	public String registInput(final HttpSession session) {
		//セッションスコープの内容をuserFormに入れる
		UserForm userForm = (UserForm) session.getAttribute("userForm");
		//もし、セッションスコープ内に情報がなく、userFormが空になっているならば、InputInitで行った処理を行う。
		if (userForm == null) {
			userForm = new UserForm();
			userForm.setAuthority(2);
			session.setAttribute("userForm", userForm);
		}
		return "redirect:/client/user/regist/input";
	}

	/**処理３：会員登録入力画面を表示する処理。
	 * セッションに保持している入力内容および
	 * バリデーション結果をモデルに設定
	 * @author :加藤拓海
	 * @param model　ビューに渡すモデル
	 * @param session　セッション情報
	 * @return:会員登録入力画面
	 */
	@GetMapping(path = "/client/user/regist/input")
	public String registInputView(final Model model, final HttpSession session) {
		//セッションスコープの内容をリクエストスコープに入れる
		model.addAttribute("userForm", session.getAttribute("userForm"));
		//エラー情報を取得
		BindingResult result = (BindingResult) session.getAttribute("result");
		//もしエラー情報が存在しているならばエラーの情報をThymeleafへ示す。
		if (result != null) {
			model.addAttribute("org.springframework.validation.BindingResult.userForm", result);
			//エラー情報を削除する
			session.removeAttribute("result");
		}
		return "client/user/regist_input";
	}

	/**
	 * 処理４：入力内容のバリデーションを実施し、確認画面への遷移判定を行う。
	 * 入力内容をセッションに保存し、入力エラーが存在する場合は入力画面へ
	 * 存在しない場合は確認画面へリダイレクト
	 * @author :加藤拓海
	 * @param session　セッション情報
	 * @param userForm　会員情報入力フォーム
	 * @param result　バリデーション結果
	 * @return　エラーの有無に応じた遷移先（有→入力画面表示処理へ　無→会員登録確認画面表示処理へ）
	 */
	@PostMapping(path = "/client/user/regist/check")
	public String registCheck(HttpSession session, @Valid @ModelAttribute UserForm userForm,
			BindingResult result) {
		UserForm lastUserForm = (UserForm) session.getAttribute("userForm");
		if (userForm.getAuthority() == null) {
			userForm.setAuthority(lastUserForm.getAuthority());
		}
		session.setAttribute("userForm", userForm);
		if (result.hasErrors()) {
			session.setAttribute("result", result);
			return "redirect:/client/user/regist/input";
		} else {
			return "redirect:/client/user/regist/check";
		}
	}

	/**
	 * 会員登録確認画面を表示。
	 * セッションに保持している内容をモデルへ設定
	 * @author :加藤拓海
	 * @param model　ビューへ渡すモデル
	 * @param session　セッション情報
	 * @return　会員登録確認画面
	 */
	@GetMapping(path = "/client/user/regist/check")
	public String registCheckView(final Model model, final HttpSession session) {
		UserForm userForm = (UserForm) session.getAttribute("userForm");
		model.addAttribute("userForm", userForm);
		return "client/user/regist_check";
	}

	/**
	 * 会員情報の登録を行う。
	 * セッションに保持している内容をエンティティへ移し替え、データベースへ登録。
	 * 登録後は入力フォーム情報を削除し、登録した会員情報をセッションで受け取る。
	 * @author :加藤拓海
	 * @param session　セッション情報
	 * @return　登録完了表示処理へ
	 */
	@PostMapping(path = "/client/user/regist/complete")
	public String registComplete(final HttpSession session) {
		UserForm userForm = (UserForm) session.getAttribute("userForm");
		if (userForm == null) {
			return "redirect:/login";
		}
		User user = new User();
		user.setEmail(userForm.getEmail());
		user.setPassword(userForm.getPassword());
		user.setName(userForm.getName());
		user.setPostalCode(userForm.getPostalCode());
		user.setAddress(userForm.getAddress());
		user.setPhoneNumber(userForm.getPhoneNumber());
		user.setAuthority(userForm.getAuthority());
		userRepository.save(user);

		UserBean userBean = new UserBean();
		userBean.setId(user.getId());
		userBean.setName(user.getName());
		userBean.setAuthority(user.getAuthority());

		session.removeAttribute("userForm");
		session.setAttribute("user", userBean);
		return "redirect:/client/user/regist/complete";
	}

	/**
	 * 登録完了画面表示処理
	 * @return　会員登録完了画面
	 */
	@GetMapping(path = "/client/user/regist/complete")
	public String registCompleteView(final HttpSession session) {
		UserBean userForm = (UserBean) session.getAttribute("user");
		if (userForm == null) {
			return "redirect:/login";
		}
		return "client/user/regist_complete";
	}
}
