package jp.co.SurveyMaker.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jp.co.SurveyMaker.Form.SurveyCategoryUpdateForm;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class SurveyCategoryController {
	
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
		mav.setViewName("redirect:/surveyContentList/contentDetail?id=1");
		
		return mav;
	}
	
	@GetMapping("/surveyContentDetail/surveyCategoryUpdate")
	public ModelAndView surveyCategoryUpdate(
			HttpServletRequest request,
			HttpSession session ,
			@RequestParam(value="id", required = true, defaultValue="-1") Integer id) throws Exception {
		ModelAndView mav = new ModelAndView();
		
		mav.addObject("surveyCategoryUpdateForm", new SurveyCategoryUpdateForm());
		mav.setViewName("/surveyCategoryUpdate");
		
		return mav;
	}
	
	@PostMapping("/surveyContentDetail/surveyCategoryUpdate/exec")
	public ModelAndView surveyCategoryUpdateExec(
			HttpServletRequest request,
			HttpSession session ,
			SurveyCategoryUpdateForm surveyCategoryUpdateForm) throws Exception {
		ModelAndView mav = new ModelAndView();
		
		mav.addObject("surveyCategoryUpdateForm", new SurveyCategoryUpdateForm());
		mav.setViewName("redirect:/surveyContentList/contentDetail?id=1");
		
		return mav;
	}
	
	@GetMapping("/surveyContentDetail/surveyCategoryDelete")
	public ModelAndView surveyCategoryDelete(
			HttpServletRequest request,
			HttpSession session ,
			@RequestParam(value="id", required = true, defaultValue="-1") Integer id) throws Exception {
		ModelAndView mav = new ModelAndView();
		
		mav.addObject("surveyCategoryUpdateForm", new SurveyCategoryUpdateForm());
		mav.setViewName("redirect:/surveyContentList/contentDetail?id=1");
		
		return mav;
	}
}
