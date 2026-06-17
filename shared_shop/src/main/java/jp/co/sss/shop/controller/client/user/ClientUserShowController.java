package jp.co.sss.shop.controller.client.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpSession;
import jp.co.sss.shop.bean.UserBean;
import jp.co.sss.shop.entity.User;
import jp.co.sss.shop.repository.UserRepository;

//会員詳細表示
@Controller
public class ClientUserShowController {

	@Autowired
	UserRepository userRepository;

	/**
	 * 会員詳細表示
	 * セッションに保持されているログインユーザ情報を元に、
	 * DBから最新のユーザ情報を取得し、UserBeanに設定し画面へ渡す。
	 * セッションタイムアウトが起こった際はログイン画面に遷移
	 * 
	 * 
	 *
	 * @author 清水美根瑠
	 * @param session ログイン情報保持
	 * @param model Viewとの値受渡し
	 * @return clitent/user/detail会員詳細表示画面（client/user/detail）
	 *	       タイムアウト時はログイン画面（redirect:/login）
	 * 
	 * 
	 */
	@RequestMapping(path = "/client/user/detail")
	public String userDtail(HttpSession session, Model model) {
		UserBean userBean = (UserBean) session.getAttribute("user");

		if (userBean == null) {
			return "redirect:/login";
		}

		User loginUserDetail = userRepository.getReferenceById(userBean.getId());

		userBean.setEmail(loginUserDetail.getEmail());
		userBean.setName(loginUserDetail.getName());
		userBean.setPostalCode(loginUserDetail.getPostalCode());
		userBean.setAddress(loginUserDetail.getAddress());
		userBean.setPhoneNumber(loginUserDetail.getPhoneNumber());
		model.addAttribute("userBean", userBean);

		return "client/user/detail";
	}

}
