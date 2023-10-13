package jp.co.SurveyMaker.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jp.co.SurveyMaker.Form.QuestionContentUpdateForm;
import jp.co.SurveyMaker.Form.SurveyCategoryUpdateForm;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class SurveyQuestionController {
	
	@GetMapping("/surveyContentDetail/questionContentRegist")
	public ModelAndView questionContentRegist(
			HttpServletRequest request,
			HttpSession session ,
			@RequestParam(value="id", required = true, defaultValue="-1") Integer id) throws Exception {
		ModelAndView mav = new ModelAndView();
		
		mav.addObject("questionContentUpdateForm", new QuestionContentUpdateForm());
		mav.setViewName("/questionContentRegist");
		
		return mav;
	}
	
	@PostMapping("/surveyContentDetail/questionContentRegist/exec")
	public ModelAndView questionContentRegistExec(
			HttpServletRequest request,
			HttpSession session ,
			QuestionContentUpdateForm questionContentUpdateForm) throws Exception {
		ModelAndView mav = new ModelAndView();
		
		mav.addObject("surveyCategoryUpdateForm", new SurveyCategoryUpdateForm());
		mav.setViewName("redirect:/surveyContentList/contentDetail?id=1");
		
		return mav;
	}
	
	@GetMapping("/surveyContentDetail/questionContentUpdate")
	public ModelAndView questionContentUpdate(
			HttpServletRequest request,
			HttpSession session ,
			@RequestParam(value="question", required = true, defaultValue="-1") Integer question) throws Exception {
		ModelAndView mav = new ModelAndView();
		
		mav.addObject("questionContentUpdateForm", new QuestionContentUpdateForm());
		mav.setViewName("/questionContentUpdate");
		
		return mav;
	}
	
	@PostMapping("/surveyContentDetail/questionContentUpdate/exec")
	public ModelAndView questionContentUpdateExec(
			HttpServletRequest request,
			HttpSession session ,
			QuestionContentUpdateForm questionContentUpdateForm) throws Exception {
		ModelAndView mav = new ModelAndView();
		
		mav.addObject("surveyCategoryUpdateForm", new SurveyCategoryUpdateForm());
		mav.setViewName("redirect:/surveyContentList/contentDetail?id=1");
		
		return mav;
	}
	
	@GetMapping("/surveyContentDetail/questionContentDelete")
	public ModelAndView questionContentDelete(
			HttpServletRequest request,
			HttpSession session ,
			@RequestParam(value="question", required = true, defaultValue="-1") Integer question) throws Exception {
		ModelAndView mav = new ModelAndView();
		
		mav.addObject("questionContentUpdateForm", new QuestionContentUpdateForm());
		mav.setViewName("redirect:/surveyContentList/contentDetail?id=1");
		
		return mav;
	}
}
