package jp.co.SurveyMaker.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpSession;
import jp.co.SurveyMaker.Constants.CommonConstants;
import jp.co.SurveyMaker.Form.LoginForm;
import jp.co.SurveyMaker.Service.LoginService;
import jp.co.SurveyMaker.Service.Entity.User;
import jp.co.SurveyMaker.Util.StringUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class LoginController {
	@Autowired
	private LoginService loginService;
	
	@GetMapping("/")
	public ModelAndView init(HttpSession session) throws Exception {

		ModelAndView mav = new ModelAndView();
		mav.addObject("loginForm", new LoginForm());
		mav.setViewName("login");

		return mav;
	}

	@PostMapping("/login")
	public ModelAndView login(HttpSession session, LoginForm loginForm) throws Exception {

		ModelAndView mav = new ModelAndView();
		
		User user = new User();
		user.setLoginId(loginForm.getLoginId());
		user.setPassword(StringUtil.getEncryptedStr(loginForm.getPassword()));
		
		User resultUser = null;
		try {
			resultUser = loginService.login(user);
			
			// セッションクリア
			session.removeAttribute(CommonConstants.SESSION_KEY_USER_LOGIN);
			session.removeAttribute(CommonConstants.SESSION_KEY_CONTENT_CONDITION);
			
		} catch (Exception e) {
			// ログイン失敗
			mav.addObject("loginForm", new LoginForm());
			mav.setViewName("login");
			mav.addObject("message", "ユーザ、パスワードでログイン失敗しました。");
			return mav;
		}
		
		session.setAttribute(CommonConstants.SESSION_KEY_USER_LOGIN, resultUser);
		
		mav.setViewName("redirect:/surveyContentList");

		return mav;
	}
	
	@GetMapping("/logout")
	public ModelAndView logout(HttpSession session) throws Exception {

		ModelAndView mav = new ModelAndView();
		mav.setViewName("redirect:/");

		session.removeAttribute(CommonConstants.SESSION_KEY_USER_LOGIN);

		return mav;
	}
}
