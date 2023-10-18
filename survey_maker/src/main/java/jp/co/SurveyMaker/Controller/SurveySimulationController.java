package jp.co.SurveyMaker.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jp.co.SurveyMaker.Form.SurveyResultForm;
import jp.co.SurveyMaker.Form.SurveySimulationForm;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class SurveySimulationController {
	
	@GetMapping("/surveyContentDetail/surveySimulation")
	public ModelAndView surveySimulation(
			HttpServletRequest request,
			HttpSession session ,
			@RequestParam(value="id", required = true, defaultValue="-1") Integer id) throws Exception {
		ModelAndView mav = new ModelAndView();
		
		mav.addObject("surveySimulationForm", new SurveySimulationForm());
		
		if(id==1) {
			// 単数
			mav.setViewName("/surveySimulationForSingular");
		}else if(id==2) {
			// 複数
			mav.setViewName("/surveySimulationForComplex");
		}else {
			// フロー
			mav.setViewName("/surveySimulationForFlow");
		}
		
		return mav;
	}
	
	@PostMapping("/surveyContentDetail/surveySimulationForSingular/result")
	public ModelAndView surveySimulationForSingularResult(
			HttpServletRequest request,
			HttpSession session ,
			SurveySimulationForm surveySimulationForm) throws Exception {
		ModelAndView mav = new ModelAndView();
		
		mav.addObject("surveyResultForm", new SurveyResultForm());
		
		mav.setViewName("/surveySimulationResultForSingular");
		
		return mav;
	}
	
	@PostMapping("/surveyContentDetail/surveySimulationForComplex/result")
	public ModelAndView surveySimulationForComplexResult(
			HttpServletRequest request,
			HttpSession session ,
			SurveySimulationForm surveySimulationForm) throws Exception {
		ModelAndView mav = new ModelAndView();
		
		mav.addObject("surveyResultForm", new SurveyResultForm());
		
		mav.setViewName("/surveySimulationResultForComplex");
		
		return mav;
	}
	
	@GetMapping("/surveyContentDetail/surveySimulationForFlow/result")
	public ModelAndView surveySimulationForFlowResult(
			HttpServletRequest request,
			HttpSession session ,
			@RequestParam(value="id", required = true, defaultValue="-1") Integer id) throws Exception {
		ModelAndView mav = new ModelAndView();
		
		mav.addObject("surveyResultForm", new SurveyResultForm());
		
		mav.setViewName("/surveySimulationResultForFlow");
		
		return mav;
	}
}
