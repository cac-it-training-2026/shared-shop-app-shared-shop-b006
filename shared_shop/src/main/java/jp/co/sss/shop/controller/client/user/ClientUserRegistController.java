package jp.co.sss.shop.controller.client.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import jakarta.servlet.http.HttpSession;
import jp.co.sss.shop.form.UserForm;
import jp.co.sss.shop.repository.UserRepository;

@Controller
public class ClientUserRegistController {
	@Autowired
	UserRepository userRepository;
	@Autowired
	HttpSession session;

	@GetMapping("/client/user/regist/input/init")
	public String registInputInit(final HttpSession session) {
		UserForm userForm = new UserForm();
		userForm.setAuthority(2);
		session.setAttribute("userForm", userForm);
		return "redirect:/client/user/regist/input";
	}

	@PostMapping(path = "/client/user/regist/input")
	public String registInput(final HttpSession session) {
		UserForm userForm = (UserForm) session.getAttribute("userForm");
		if (userForm == null) {
			userForm = new UserForm();
			userForm.setAuthority(2);
			session.setAttribute("userForm", userForm);
		}
		return "redirect:/client/user/regist/input";
	}

	@GetMapping(path = "/client/user/regist/input")
	public String registInputView(final Model model, final HttpSession session) {
		model.addAttribute("userForm", session.getAttribute("userForm"));
		BindingResult result = (BindingResult) session.getAttribute("result");
		if (result != null) {
			model.addAttribute(
					"org.springframework.validation.BindingResult.userForm",
					result);

			session.removeAttribute("result");
		}
		return "client/user/regist_input";
	}

	//	@PostMapping(path = "/client/user/regist/check ")
	//	public String registCheck(HttpSession session, @Valid @ModelAttribute UserForm userForm,
	//			BindingResult result) {
	//		if (result.hasErrors()) {
	//			return "redirect:/client/user/regist_input";
	//		} else {
	//			session.setAttribute("userForm", userForm);
	//			return "client/user/regist_check";
	//		}
	//	}
}
