package jp.co.sss.shop.validator;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;

import jp.co.sss.shop.annotation.LoginCheck;
import jp.co.sss.shop.bean.UserBean;
import jp.co.sss.shop.entity.User;
import jp.co.sss.shop.repository.UserRepository;
import java.time.LocalDateTime;

import jp.co.sss.shop.util.Constant;

/**
 * ログインチェックの独自検証クラス
 *
 * @author System Shared
 */
public class LoginValidator implements ConstraintValidator<LoginCheck, Object> {
	private String email;
	private String password;

	@Autowired
	UserRepository userRepository;

	@Autowired
	HttpSession session;

	@Override
	public void initialize(LoginCheck annotation) {
		this.email = annotation.fieldEmail();
		this.password = annotation.fieldPassword();
	}

	@Override
	public boolean isValid(Object value, ConstraintValidatorContext context) {
		BeanWrapper beanWrapper = new BeanWrapperImpl(value);
		String emailProp = (String) beanWrapper.getPropertyValue(this.email);
		String passwordProp = (String) beanWrapper.getPropertyValue(this.password);

		User user = userRepository.findByEmailAndDeleteFlag(emailProp, Constant.NOT_DELETED);

		if (user == null) {
			return false;
		}

		// アカウントロック判定
		if (user.getLockTime() != null) {
			if (user.getLockTime().isAfter(LocalDateTime.now())) {
				// ロック中の場合
				context.disableDefaultConstraintViolation();
				context.buildConstraintViolationWithTemplate("{LoginCheck.locked}").addConstraintViolation();
				return false;
			} else {
				// ロック解除時間が経過している場合、失敗回数とロック時間をリセット
				user.setLoginFailCount(0);
				user.setLockTime(null);
			}
		}

		if (passwordProp.equals(user.getPassword())) {
			// ログイン成功
			UserBean userBean = new UserBean();

			userBean.setId(user.getId());
			userBean.setName(user.getName());
			userBean.setAuthority(user.getAuthority());

			// ログイン成功時に失敗回数をリセット
			user.setLoginFailCount(0);
			user.setLockTime(null);
			userRepository.save(user);

			// セッションスコープにログインしたユーザの情報を登録
			session.setAttribute("user", userBean);
			return true;
		} else {
			// ログイン失敗
			int failCount = (user.getLoginFailCount() != null ? user.getLoginFailCount() : 0) + 1;
			user.setLoginFailCount(failCount);

			if (failCount >= Constant.MAX_LOGIN_FAIL_COUNT) {
				// 失敗回数が上限に達した場合、アカウントをロック
				user.setLockTime(LocalDateTime.now().plusMinutes(Constant.ACCOUNT_LOCK_MINUTES));
			}
			userRepository.save(user);

			return false;
		}
	}
}
