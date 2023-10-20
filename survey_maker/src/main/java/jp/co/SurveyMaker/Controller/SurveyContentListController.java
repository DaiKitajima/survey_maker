package jp.co.SurveyMaker.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jp.co.SurveyMaker.Constants.CommonConstants;
import jp.co.SurveyMaker.Form.SurveyContentDetailForm;
import jp.co.SurveyMaker.Form.SurveyContentListForm;
import jp.co.SurveyMaker.Service.SurveyContentListService;
import jp.co.SurveyMaker.Service.SurveyPatternService;
import jp.co.SurveyMaker.Service.Entity.SurveyManagement;
import jp.co.SurveyMaker.Service.Entity.SurveyPattern;
import jp.co.SurveyMaker.Service.Entity.User;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class SurveyContentListController {
	 
	@Autowired
	private SurveyContentListService surveyContentListService;
	
	@Autowired
	private SurveyPatternService surveyPatternService;
	
	//一覧表示
	@GetMapping("/surveyContentList")
	public ModelAndView init(
			HttpServletRequest request,
			HttpSession session,
			@PageableDefault(page = 0, size = 10000)Pageable pageable,
			@RequestParam(value="isReturn", required = false, defaultValue="false") boolean isReturn ) throws Exception {
		ModelAndView mav = new ModelAndView();

		// セッションからユーザ情報取得
		User user = (User) session.getAttribute(CommonConstants.SESSION_KEY_USER_LOGIN);
		
		// 検索条件設定
		SurveyManagement condition = new SurveyManagement();
		condition.setUserId(user.getId());
		condition.setSurveyName(null);
		condition.setSurveyPatternId(null);
		
		SurveyContentListForm surveyContentListForm = new SurveyContentListForm();
		surveyContentListForm.setSurveyContentList(surveyContentListService.surveyContentSearch(condition)) ;
		
		List<SurveyPattern> patterns = surveyPatternService.getAllPattern();
		mav.addObject("patternLst", patterns);
		mav.addObject("surveyContentListForm", surveyContentListForm);
		mav.setViewName("/surveyContentList");
		
		return mav;
	}
	
	@PostMapping("/surveyContentList/search")
	public ModelAndView search(
			HttpServletRequest request,
			HttpSession session,
			SurveyContentListForm surveyContentListForm ) throws Exception {
		ModelAndView mav = new ModelAndView();
		
		// セッションからユーザ情報取得
		User user = (User) session.getAttribute(CommonConstants.SESSION_KEY_USER_LOGIN);
		
		// 検索条件設定
		SurveyManagement condition = new SurveyManagement();
		condition.setUserId(user.getId());
		condition.setSurveyName(surveyContentListForm.getSurveyNameForSearch());
		condition.setSurveyPatternId(surveyContentListForm.getSurveyPatternIdForSearch());
		
		surveyContentListForm.setSurveyContentList(surveyContentListService.surveyContentSearch(condition)) ;
		
		List<SurveyPattern> patterns = surveyPatternService.getAllPattern();
		mav.addObject("patternLst", patterns);
		
		mav.addObject("surveyContentListForm", surveyContentListForm);
		mav.setViewName("/surveyContentList");
		
		return mav;
	}
	
	@GetMapping("/surveyContentList/contentDetail")
	public ModelAndView surveyContentDetail(
			HttpServletRequest request,
			HttpSession session,
			@RequestParam(value="contentId", required = true, defaultValue="-1") Integer contentId ) throws Exception {
		ModelAndView mav = new ModelAndView();
		
		mav.addObject("surveyContentDetailForm", new SurveyContentDetailForm());
		mav.setViewName("/surveyContentDetail");
		
		return mav;
	}
	
	@GetMapping("/surveyContentList/contentDelete")
	public ModelAndView surveyContentDelete(
			HttpServletRequest request,
			HttpSession session,
			@RequestParam(value="id", required = true, defaultValue="-1") Integer id ) throws Exception {
		ModelAndView mav = new ModelAndView();
		
		mav.addObject("surveyContentDetailForm", new SurveyContentDetailForm());
		mav.setViewName("redirect:/surveyContentList");
		
		return mav;
	}
}
