package jp.co.SurveyMaker.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jp.co.SurveyMaker.Form.QuestionLinkUpdateForm;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class SurveyQuestionLinkController {
	
	@GetMapping("/surveyContentDetail/questionLinkRegist")
	public ModelAndView questionLinkRegist(
			HttpServletRequest request,
			HttpSession session ,
			@RequestParam(value="id", required = true, defaultValue="-1") Integer id) throws Exception {
		ModelAndView mav = new ModelAndView();
		
		mav.addObject("questionLinkUpdateForm", new QuestionLinkUpdateForm());
		mav.setViewName("/questionLinkRegist");
		
		return mav;
	}
	
	@PostMapping("/surveyContentDetail/questionLinkRegist/exec")
	public ModelAndView questionLinkRegistExec(
			HttpServletRequest request,
			HttpSession session ,
			QuestionLinkUpdateForm questionLinkUpdateForm) throws Exception {
		ModelAndView mav = new ModelAndView();
		
		mav.addObject("questionLinkUpdateForm", new QuestionLinkUpdateForm());
		mav.setViewName("redirect:/surveyContentList/contentDetail?id=1");
		
		return mav;
	}
	
	@GetMapping("/surveyContentDetail/questionLinkUpdate")
	public ModelAndView questionLinkUpdate(
			HttpServletRequest request,
			HttpSession session ,
			@RequestParam(value="question", required = true, defaultValue="-1") Integer question) throws Exception {
		ModelAndView mav = new ModelAndView();
		
		mav.addObject("questionLinkUpdateForm", new QuestionLinkUpdateForm());
		mav.setViewName("/questionLinkUpdate");
		
		return mav;
	}
	
	@PostMapping("/surveyContentDetail/questionLinkUpdate/exec")
	public ModelAndView questionLinkUpdateExec(
			HttpServletRequest request,
			HttpSession session ,
			QuestionLinkUpdateForm questionLinkUpdateForm) throws Exception {
		ModelAndView mav = new ModelAndView();
		
		mav.addObject("questionLinkUpdateForm", new QuestionLinkUpdateForm());
		mav.setViewName("redirect:/surveyContentList/contentDetail?id=1");
		
		return mav;
	}
}
