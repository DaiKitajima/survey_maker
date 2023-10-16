package jp.co.SurveyMaker.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jp.co.SurveyMaker.Form.SurveySimulationForm;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class SurveySimulationController {
	
	@GetMapping("/surveyContentDetail/surveySimulation")
	public ModelAndView surveyContentDetail(
			HttpServletRequest request,
			HttpSession session ,
			@RequestParam(value="id", required = true, defaultValue="-1") Integer id) throws Exception {
		ModelAndView mav = new ModelAndView();
		
		mav.addObject("surveySimulationForm", new SurveySimulationForm());
		
		mav.setViewName("/surveySimulationForSingular");
//		mav.setViewName("/surveySimulationForComplex");
//		mav.setViewName("/surveySimulationForFlow");
		
		return mav;
	}
}
