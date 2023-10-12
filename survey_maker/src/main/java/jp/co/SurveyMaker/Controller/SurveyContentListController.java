package jp.co.SurveyMaker.Controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jp.co.SurveyMaker.Form.SurveyContentDetailForm;
import jp.co.SurveyMaker.Form.SurveyContentListForm;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class SurveyContentListController {
	 
	//一覧表示
	@GetMapping("/surveyContentList")
	public ModelAndView init(
			HttpServletRequest request,
			HttpSession session,
			@PageableDefault(page = 0, size = 10000)Pageable pageable,
			@RequestParam(value="isReturn", required = false, defaultValue="false") boolean isReturn ) throws Exception {
		ModelAndView mav = new ModelAndView();
		
		mav.addObject("surveyContentListForm", new SurveyContentListForm());
		mav.setViewName("/surveyContentList");
		
		return mav;
	}
	
	@PostMapping("/surveyContentList/search")
	public ModelAndView search(
			HttpServletRequest request,
			HttpSession session,
			SurveyContentListForm surveyContentListForm ) throws Exception {
		ModelAndView mav = new ModelAndView();
		
		mav.addObject("surveyContentListForm", new SurveyContentListForm());
		mav.setViewName("/surveyContentList");
		
		return mav;
	}
	
	@GetMapping("/surveyContentList/contentDetail")
	public ModelAndView surveyContentDetail(
			HttpServletRequest request,
			HttpSession session,
			@RequestParam(value="id", required = true, defaultValue="-1") Integer id ) throws Exception {
		ModelAndView mav = new ModelAndView();
		
		mav.addObject("surveyContentDetailForm", new SurveyContentDetailForm());
		mav.setViewName("/surveyContentDetail");
		
		return mav;
	}
}
