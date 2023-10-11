package jp.co.SurveyMaker.Interceptor;

import org.hibernate.SessionException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import jp.co.SurveyMaker.Form.LoginForm;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
public class GloableException  {

	@ExceptionHandler(Exception.class)
	public ModelAndView  toError(Exception e) {
	    log.debug(e.getMessage());
	    ModelAndView mav = new ModelAndView();
	    
	    // session失効判断
	    if(e instanceof SessionException){
	        log.debug("ユーザ有効期限切れ",e.getMessage());
	        mav.addObject("message","ユーザ有効期限切れ、再度ログインしてください。");
	        mav.addObject("loginForm", new LoginForm());
	        mav.setViewName("login");
	        return mav;
	    }else {
	        log.error("エラー発生:",e);
	        mav.setViewName("error");
	        return  mav;
		}
	}
}