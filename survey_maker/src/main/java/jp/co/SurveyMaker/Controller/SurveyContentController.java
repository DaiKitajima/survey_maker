package jp.co.SurveyMaker.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jp.co.SurveyMaker.Form.SurveyContentUpdateForm;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class SurveyContentController {
	
	@GetMapping("/surveyContentDetail/surveyContentRegist")
	public ModelAndView surveyContentDetail(
			HttpServletRequest request,
			HttpSession session ) throws Exception {
		ModelAndView mav = new ModelAndView();
		
		mav.addObject("surveyContentUpdateForm", new SurveyContentUpdateForm());
		mav.setViewName("/surveyContentRegist");
		
		return mav;
	}
}
