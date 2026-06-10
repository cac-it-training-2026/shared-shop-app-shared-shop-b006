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
import jp.co.sss.shop.entity.User;
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

	@PostMapping(path = "/client/user/regist/check")
	public String registCheck(HttpSession session, @Valid @ModelAttribute UserForm userForm,
			BindingResult result) {
		UserForm lastUserForm = (UserForm) session.getAttribute("userForm");
		if (lastUserForm == null) {
			return "redirect:/syserror";
		}
		if (userForm.getAuthority() == null) {
			userForm.setAuthority(lastUserForm.getAuthority());
		}
		session.setAttribute("userForm", userForm);
		session.setAttribute("result", result);
		if (result.hasErrors()) {
			return "redirect:/client/user/regist/input";
		} else {
			return "redirect:/client/user/regist/check";
		}
	}

	@GetMapping(path = "/client/user/regist/check")
	public String registCheckView(final Model model, final HttpSession session) {
		UserForm userForm = (UserForm) session.getAttribute("userForm");
		model.addAttribute("userForm", userForm);
		return "client/user/regist_check";
	}

	@PostMapping(path = "/client/user/regist/complete")
	public String registComplete(final HttpSession session) {
		UserForm userForm = (UserForm) session.getAttribute("userForm");
		if (userForm == null) {
			return "redirect:/syserror";
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
		session.removeAttribute("userForm");
		session.setAttribute("user", user);
		return "redirect:/client/user/regist/complete";
	}

	@GetMapping(path = "/client/user/regist/complete")
	public String registCompleteView() {

		return "client/user/regist_complete";
	}
}
