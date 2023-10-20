package jp.co.SurveyMaker.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jp.co.SurveyMaker.Constants.CommonConstants;
import jp.co.SurveyMaker.Form.SurveyContentUpdateForm;
import jp.co.SurveyMaker.Service.SurveyContentService;
import jp.co.SurveyMaker.Service.SurveyPatternService;
import jp.co.SurveyMaker.Service.Entity.SurveyManagement;
import jp.co.SurveyMaker.Service.Entity.SurveyPattern;
import jp.co.SurveyMaker.Service.Entity.User;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class SurveyContentController {
	
	@Autowired
	private SurveyContentService surveyContentService;
	
	@Autowired
	private SurveyPatternService surveyPatternService;
	
	@GetMapping("/surveyContentDetail/surveyContentRegist")
	public ModelAndView surveyContentRegistl(
			HttpServletRequest request,
			HttpSession session ) throws Exception {
		ModelAndView mav = new ModelAndView();
		// セッションからユーザ情報取得
		User user = (User) session.getAttribute(CommonConstants.SESSION_KEY_USER_LOGIN);
		
		List<SurveyPattern> patterns = surveyPatternService.getAllPattern();
		mav.addObject("patternLst", patterns);
		
		SurveyContentUpdateForm surveyContentUpdateForm = new SurveyContentUpdateForm();
		surveyContentUpdateForm.setUserId(user.getId());
		mav.addObject("surveyContentUpdateForm", surveyContentUpdateForm);
		
		mav.setViewName("/surveyContentRegist");
		
		return mav;
	}
	
	@PostMapping("/surveyContentDetail/surveyContentRegist/exec")
	public ModelAndView surveyContentRegistExec(
			HttpServletRequest request,
			HttpSession session ,
			SurveyContentUpdateForm surveyContentUpdateForm) throws Exception {
		ModelAndView mav = new ModelAndView();
		
		SurveyManagement surveyContent = new SurveyManagement ();
		this.convertSurveyContentFormToEnt(surveyContentUpdateForm, surveyContent);
		surveyContentService.surveyContentRegist(surveyContent);
		
		mav.setViewName("redirect:/surveyContentList");
		
		return mav;
	}
	
	private void convertSurveyContentFormToEnt(SurveyContentUpdateForm surveyContentUpdateForm,
			SurveyManagement surveyContent) {
		surveyContent.setId(surveyContentUpdateForm.getId());
		surveyContent.setUserId(surveyContentUpdateForm.getUserId());
		surveyContent.setSurveyColor(surveyContentUpdateForm.getSurveyColor());
		// TODO 画像保存仕様待ち
		surveyContent.setSurveyImage(null);
		surveyContent.setSurveyName(surveyContentUpdateForm.getSurveyName());
		surveyContent.setSurveyPatternId(surveyContentUpdateForm.getSurveyPatternId());
		}

	@GetMapping("/surveyContentDetail/surveyContentUpdate")
	public ModelAndView surveyContentUpdate(
			HttpServletRequest request,
			HttpSession session ,
			@RequestParam(value="contentId", required = true, defaultValue="-1") Integer contentId) throws Exception {
		ModelAndView mav = new ModelAndView();
		
		SurveyManagement surveyContent = surveyContentService.getSurveyContentById(contentId);
		SurveyContentUpdateForm surveyContentUpdateForm = new SurveyContentUpdateForm();
		this.convertSurveyContentEntityToForm(surveyContent, surveyContentUpdateForm);
		mav.addObject("surveyContentUpdateForm", surveyContentUpdateForm);
		mav.setViewName("/surveyContentUpdate");
		
		return mav;
	}
	
	private void convertSurveyContentEntityToForm(SurveyManagement surveyContent,
			SurveyContentUpdateForm surveyContentUpdateForm) {
		surveyContentUpdateForm.setId(surveyContent.getId());
		surveyContentUpdateForm.setUserId(surveyContent.getUserId());
		surveyContentUpdateForm.setSurveyColor(surveyContent.getSurveyColor());
		// TODO 画像保存仕様待ち
		surveyContentUpdateForm.setSurveyImage(null);
		surveyContentUpdateForm.setSurveyName(surveyContent.getSurveyName());
		surveyContentUpdateForm.setSurveyPatternId(surveyContent.getSurveyPatternId());
	}

	@PostMapping("/surveyContentDetail/surveyContentUpdate/exec")
	public ModelAndView surveyContentUpdateExec(
			HttpServletRequest request,
			HttpSession session ,
			SurveyContentUpdateForm surveyContentUpdateForm) throws Exception {
		ModelAndView mav = new ModelAndView();
		
		mav.addObject("surveyContentUpdateForm", new SurveyContentUpdateForm());
		mav.setViewName("redirect:/surveyContentList/contentDetail?id=1");
		
		return mav;
	}
}
