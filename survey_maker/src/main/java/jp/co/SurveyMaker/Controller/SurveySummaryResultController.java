package jp.co.SurveyMaker.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jp.co.SurveyMaker.Form.SurveyCategoryUpdateForm;
import jp.co.SurveyMaker.Form.SurveySummaryResultUpdateForm;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class SurveySummaryResultController {
	
	@GetMapping("/surveyContentDetail/summaryResultRegist")
	public ModelAndView summaryResultRegist(
			HttpServletRequest request,
			HttpSession session ,
			@RequestParam(value="id", required = true, defaultValue="-1") Integer id) throws Exception {
		ModelAndView mav = new ModelAndView();
		
		mav.addObject("surveyResultUpdateForm", new SurveySummaryResultUpdateForm());
		mav.setViewName("summaryResultRegist");
		
		return mav;
	}
	
	@PostMapping("/surveyContentDetail/summaryResultRegist/exec")
	public ModelAndView summaryResultRegistExec(
			HttpServletRequest request,
			HttpSession session ,
			SurveySummaryResultUpdateForm surveyResultUpdateForm) throws Exception {
		ModelAndView mav = new ModelAndView();
		
		mav.addObject("surveyResultUpdateForm", new SurveySummaryResultUpdateForm());
		mav.setViewName("redirect:/surveyContentList/contentDetail?contentId=" + surveyResultUpdateForm.getSurveyManagementId());
		
		return mav;
	}
	
	@GetMapping("/surveyContentDetail/summaryResultUpdate")
	public ModelAndView questionContentUpdate(
			HttpServletRequest request,
			HttpSession session ,
			@RequestParam(value="id", required = true, defaultValue="-1") Integer id) throws Exception {
		ModelAndView mav = new ModelAndView();
		
		mav.addObject("surveyResultUpdateForm", new SurveySummaryResultUpdateForm());
		mav.setViewName("summaryResultUpdate");
		
		return mav;
	}
	
	@PostMapping("/surveyContentDetail/summaryResultUpdate/exec")
	public ModelAndView summaryResultUpdateExec(
			HttpServletRequest request,
			HttpSession session ,
			SurveySummaryResultUpdateForm surveyResultUpdateForm) throws Exception {
		ModelAndView mav = new ModelAndView();
		
		mav.addObject("surveyCategoryUpdateForm", new SurveyCategoryUpdateForm());
		mav.setViewName("redirect:/surveyContentList/contentDetail?contentId=" + surveyResultUpdateForm.getSurveyManagementId());
		
		return mav;
	}
}
