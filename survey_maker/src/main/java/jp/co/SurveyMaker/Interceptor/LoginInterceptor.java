package jp.co.SurveyMaker.Interceptor;

import org.hibernate.SessionException;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jp.co.SurveyMaker.Constants.CommonConstants;
import jp.co.SurveyMaker.Service.Entity.User;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class LoginInterceptor implements HandlerInterceptor {

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
			HttpSession session = request.getSession();
			// セッションからユーザ情報取得
			User user = (User) session.getAttribute(CommonConstants.SESSION_KEY_USER_LOGIN);
	
			// session失効
			if(user == null){
			    log.info("session失効");
			    throw new SessionException("ユーザ情報有効期限切れ");
			}else{
			    return true;
			}
	}
}