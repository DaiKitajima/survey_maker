package jp.co.SurveyMaker.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jp.co.SurveyMaker.Form.SurveyCategoryUpdateForm;
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
	
	@PostMapping("/surveyContentDetail/surveyContentRegist/exec")
	public ModelAndView surveyContentDetailExec(
			HttpServletRequest request,
			HttpSession session ,
			SurveyContentUpdateForm surveyContentUpdateForm) throws Exception {
		ModelAndView mav = new ModelAndView();
		
		mav.addObject("surveyContentUpdateForm", new SurveyContentUpdateForm());
		mav.setViewName("redirect:/surveyContentList");
		
		return mav;
	}
	
	@GetMapping("/surveyContentDetail/surveyContentUpdate")
	public ModelAndView surveyContentUpdate(
			HttpServletRequest request,
			HttpSession session ,
			@RequestParam(value="id", required = true, defaultValue="-1") Integer id) throws Exception {
		ModelAndView mav = new ModelAndView();
		
		mav.addObject("surveyContentUpdateForm", new SurveyContentUpdateForm());
		mav.setViewName("/surveyContentUpdate");
		
		return mav;
	}
	
	@PostMapping("/surveyContentDetail/surveyContentUpdate/exec")
	public ModelAndView surveyContentUpdateExec(
			HttpServletRequest request,
			HttpSession session ,
			SurveyContentUpdateForm surveyContentUpdateForm) throws Exception {
		ModelAndView mav = new ModelAndView();
		
		mav.addObject("surveyContentUpdateForm", new SurveyContentUpdateForm());
		mav.setViewName("redirect:/surveyContentList");
		
		return mav;
	}
	
	@GetMapping("/surveyContentDetail/surveyCategoryRegist")
	public ModelAndView surveyCategoryRegist(
			HttpServletRequest request,
			HttpSession session ,
			@RequestParam(value="id", required = true, defaultValue="-1") Integer id) throws Exception {
		ModelAndView mav = new ModelAndView();
		
		mav.addObject("surveyCategoryUpdateForm", new SurveyCategoryUpdateForm());
		mav.setViewName("/surveyCategoryRegist");
		
		return mav;
	}
	
	@PostMapping("/surveyContentDetail/surveyCategoryRegist/exec")
	public ModelAndView surveyCategoryRegistExec(
			HttpServletRequest request,
			HttpSession session ,
			SurveyCategoryUpdateForm surveyCategoryUpdateForm) throws Exception {
		ModelAndView mav = new ModelAndView();
		
		mav.addObject("surveyCategoryUpdateForm", new SurveyCategoryUpdateForm());
		mav.setViewName("redirect:/surveyContentList");
		
		return mav;
	}
	
}
