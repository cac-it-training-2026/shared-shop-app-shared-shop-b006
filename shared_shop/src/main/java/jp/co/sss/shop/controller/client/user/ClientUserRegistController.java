package jp.co.sss.shop.controller.client.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import jp.co.sss.shop.repository.UserRepository;

@Controller
public class ClientUserRegistController {
	@Autowired
	UserRepository userRepository;
}
