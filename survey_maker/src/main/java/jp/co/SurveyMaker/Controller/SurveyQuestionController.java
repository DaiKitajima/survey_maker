package jp.co.SurveyMaker.Controller;

import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jp.co.SurveyMaker.Form.QuestionContentUpdateForm;
import jp.co.SurveyMaker.Form.SurveyCategoryUpdateForm;
import jp.co.SurveyMaker.Service.SurveyCategoryService;
import jp.co.SurveyMaker.Service.SurveyQuestionService;
import jp.co.SurveyMaker.Service.Entity.SurveyQuestion;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class SurveyQuestionController {
	@Autowired
	private SurveyCategoryService surveyCategoryService;
	
	@Autowired
	private SurveyQuestionService surveyQuestionService;
	
	@GetMapping("/surveyContentDetail/questionContentRegist")
	public ModelAndView questionContentRegist(
			HttpServletRequest request,
			HttpSession session ,
			@RequestParam(value="contentId", required = true, defaultValue="-1") Integer contentId) throws Exception {
		ModelAndView mav = new ModelAndView();

		QuestionContentUpdateForm questionContentUpdateForm = new QuestionContentUpdateForm();
		questionContentUpdateForm.setSurveyManagementId(contentId);
		// 質問順番取得
		List<SurveyQuestion> questionLst = surveyQuestionService.getSurveyQuestionByContentId(contentId);
		if(questionLst != null && questionLst.size() != 0 ) {
			questionLst.stream()
								.max(Comparator.comparing(SurveyQuestion::getQuestionOrderNo))
								.ifPresent(e -> {
									questionContentUpdateForm.setQuestionOrderNo(e.getQuestionOrderNo() + 1);
								} );
		}else {
			questionContentUpdateForm.setQuestionOrderNo(1);
		}
		mav.addObject("questionContentUpdateForm", questionContentUpdateForm);
		mav.addObject("categoryLst", surveyCategoryService.getSurveyCategoryByContentId(contentId));
		
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
