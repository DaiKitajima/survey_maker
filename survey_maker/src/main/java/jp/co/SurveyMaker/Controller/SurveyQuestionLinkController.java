package jp.co.SurveyMaker.Controller;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jp.co.SurveyMaker.Constants.CommonConstants;
import jp.co.SurveyMaker.Constants.LinkType;
import jp.co.SurveyMaker.Dto.AnswerContentDto;
import jp.co.SurveyMaker.Dto.CategoryContentDto;
import jp.co.SurveyMaker.Dto.QuestionPositionDto;
import jp.co.SurveyMaker.Form.QuestionContentUpdateForm;
import jp.co.SurveyMaker.Form.QuestionLinkForm;
import jp.co.SurveyMaker.Form.QuestionLinkUpdateForm;
import jp.co.SurveyMaker.Form.SurveyCategoryUpdateForm;
import jp.co.SurveyMaker.Service.SurveyCategoryService;
import jp.co.SurveyMaker.Service.SurveyContentService;
import jp.co.SurveyMaker.Service.SurveyQuestionLinkService;
import jp.co.SurveyMaker.Service.SurveyQuestionService;
import jp.co.SurveyMaker.Service.Entity.SurveyCategory;
import jp.co.SurveyMaker.Service.Entity.SurveyManagement;
import jp.co.SurveyMaker.Service.Entity.SurveyQuestion;
import jp.co.SurveyMaker.Service.Entity.SurveyQuestionLink;
import jp.co.SurveyMaker.Service.Entity.User;
import jp.co.SurveyMaker.Util.StringUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class SurveyQuestionLinkController {
	@Autowired
	private SurveyQuestionService surveyQuestionService;
	
	@Autowired
	private SurveyContentService surveyContentService;
	
	@Autowired
	private SurveyCategoryService surveyCategoryService;
	
	@Autowired
	private SurveyQuestionLinkService surveyQuestionLinkService;
	
	@GetMapping("/surveyContentDetail/questionLinkRegist")
	public ModelAndView questionLinkRegist(
			HttpServletRequest request,
			HttpSession session ,
			@RequestParam(value="contentId", required = true, defaultValue="-1") Integer contentId) throws Exception {
		ModelAndView mav = new ModelAndView();
		// セッションからユーザ情報取得
		User user = (User) session.getAttribute(CommonConstants.SESSION_KEY_USER_LOGIN);
		SurveyManagement survey = surveyContentService.getSurveyContentByIdAndUserId(contentId, user.getId());
		mav.addObject("survey", survey);
		
		QuestionLinkUpdateForm questionLinkUpdateForm = new QuestionLinkUpdateForm();
		List<SurveyQuestion> questionLst = surveyQuestionService.getSurveyQuestionByContentIdOrderByOrderNo(contentId);
		List<QuestionContentUpdateForm> formLst = new ArrayList<QuestionContentUpdateForm>();
		if(questionLst != null && questionLst.size() != 0 ) {
			for(SurveyQuestion question : questionLst) {
				QuestionContentUpdateForm questionContentUpdateForm = new QuestionContentUpdateForm();
				this.convertEntityToQuestionForm(question, questionContentUpdateForm);
				formLst.add(questionContentUpdateForm);
			}
		}
		questionLinkUpdateForm.setQuestionFormLst(formLst);
		questionLinkUpdateForm.setSurveyManagementId(contentId);
		mav.addObject("questionLinkUpdateForm", questionLinkUpdateForm);
		
		mav.addObject("linkTypeMap", Stream.of(LinkType.values()).collect(Collectors.toMap(t->t.getCode(),  t->t.getDisplay())));
		
		// カテゴリー評価結果コンテンツ
		Type listType = new TypeToken<ArrayList<CategoryContentDto>>(){}.getType();
		List<CategoryContentDto> contentLst = (new Gson()).fromJson(surveyCategoryService.getSurveyCategoryByContentId(contentId).get(0).getSurveyCategoryContent(), listType);  
		mav.addObject("surveyResultLst", contentLst);
		
		// リファラ
		mav.addObject("referer", request.getHeader("referer"));
		mav.setViewName("/questionLinkRegist");
		
		return mav;
	}
	
	private void convertEntityToQuestionForm(SurveyQuestion question,
			QuestionContentUpdateForm questionContentUpdateForm) {
		questionContentUpdateForm.setId(question.getId());
		questionContentUpdateForm.setSurveyManagementId(question.getSurveyManagementId());	
		questionContentUpdateForm.setQuestionTitle(question.getQuestionTitle());
		questionContentUpdateForm.setQuestionOrderNo(question.getQuestionOrderNo());
		questionContentUpdateForm.setQuestionImage(question.getQuestionImage());
		// チャート図の場合、位置設定
		if(StringUtil.isNotEmpty(question.getQuestionPosition()) ) {
			// 位置
			Type type = new TypeToken<QuestionPositionDto>(){}.getType();
			QuestionPositionDto position = (new Gson()).fromJson(question.getQuestionPosition(), type);  
			questionContentUpdateForm.setPosition(position);
		}
		
		// 回答コンテンツ
		Type listType = new TypeToken<ArrayList<AnswerContentDto>>(){}.getType();
		List<AnswerContentDto> answerContentLst = (new Gson()).fromJson(question.getAnswerContent(), listType);  
		questionContentUpdateForm.setAnswerContentLst(answerContentLst);
	}
	
	@PostMapping("/surveyContentDetail/questionLinkRegist/exec")
	public ModelAndView questionLinkRegistExec(
			HttpServletRequest request,
			HttpSession session ,
			QuestionLinkUpdateForm questionLinkUpdateForm) throws Exception {
		ModelAndView mav = new ModelAndView();
		// セッションからユーザ情報取得
		User user = (User) session.getAttribute(CommonConstants.SESSION_KEY_USER_LOGIN);
		surveyContentService.getSurveyContentByIdAndUserId(questionLinkUpdateForm.getSurveyManagementId(), user.getId());
		
		List<SurveyQuestionLink> questionLinkLst = new ArrayList<SurveyQuestionLink>();
		if(questionLinkUpdateForm.getQuestionLinkLst() != null && questionLinkUpdateForm.getQuestionLinkLst().size() != 0 ) {
			this.convertQuestionLinkFormToEntity(questionLinkUpdateForm.getQuestionLinkLst(), questionLinkLst,questionLinkUpdateForm.getSurveyManagementId());
			surveyQuestionLinkService.surveyQuestionLinkLstUpdate(questionLinkLst);
		}
		
		mav.setViewName("redirect:/surveyContentList/contentDetail?contentId=" + questionLinkUpdateForm.getSurveyManagementId());
		
		return mav;
	}
	
	private void convertQuestionLinkFormToEntity(List<QuestionLinkForm> questionLinkFormLst,
			List<SurveyQuestionLink> questionLinkLst, Integer contentId) {
		if(questionLinkFormLst != null && questionLinkFormLst.size() !=0 ) {
			questionLinkFormLst.forEach( form ->{
				SurveyQuestionLink questionLink = new SurveyQuestionLink();
				questionLink.setId(form.getId());
				questionLink.setSurveyManagementId(contentId);
				questionLink.setSurveyQuestionId(form.getQuestionId());
				questionLink.setAnswerId(form.getAnswerId());
				questionLink.setLinkType(form.getLinkType());
				questionLink.setLinkTo(form.getLinkTo());
				questionLinkLst.add(questionLink);
			});
		}
	}

	@GetMapping("/surveyContentDetail/questionLinkUpdate")
	public ModelAndView questionLinkUpdate(
			HttpServletRequest request,
			HttpSession session ,
			@RequestParam(value="contentId", required = true) Integer contentId) throws Exception {
		ModelAndView mav = new ModelAndView();
		// セッションからユーザ情報取得
		User user = (User) session.getAttribute(CommonConstants.SESSION_KEY_USER_LOGIN);
		SurveyManagement survey = surveyContentService.getSurveyContentByIdAndUserId(contentId, user.getId());
		mav.addObject("survey", survey);
		
		QuestionLinkUpdateForm questionLinkUpdateForm = new QuestionLinkUpdateForm();
		List<SurveyQuestion> questionLst = surveyQuestionService.getSurveyQuestionByContentIdOrderByOrderNo(contentId);
		List<QuestionContentUpdateForm> formLst = new ArrayList<QuestionContentUpdateForm>();
		if(questionLst != null && questionLst.size() != 0 ) {
			for(SurveyQuestion question : questionLst) {
				QuestionContentUpdateForm questionContentUpdateForm = new QuestionContentUpdateForm();
				this.convertEntityToQuestionForm(question, questionContentUpdateForm);
				formLst.add(questionContentUpdateForm);
			}
		}
		questionLinkUpdateForm.setQuestionFormLst(formLst);
		
		List<SurveyQuestionLink> questionLinkLst = surveyQuestionLinkService.getSurveyQuestionLinkLst(contentId);
		List<QuestionLinkForm> linkFormLst = new ArrayList<QuestionLinkForm>();
		if(questionLinkLst != null && questionLinkLst.size() != 0 ) {
			for(SurveyQuestionLink link : questionLinkLst) {
				QuestionLinkForm linkForm = new QuestionLinkForm();
				this.convertEntityToQuestionLinkForm(link, linkForm);
				linkFormLst.add(linkForm);
			}
		}
		questionLinkUpdateForm.setQuestionLinkLst(linkFormLst);
		
		questionLinkUpdateForm.setSurveyManagementId(contentId);
		mav.addObject("questionLinkUpdateForm", questionLinkUpdateForm);
		
		mav.addObject("linkTypeMap", Stream.of(LinkType.values()).collect(Collectors.toMap(t->t.getCode(),  t->t.getDisplay())));
		
		// カテゴリー評価結果コンテンツ
		List<SurveyCategory> categoryLst = surveyCategoryService.getSurveyCategoryByContentId(contentId);
		if(categoryLst != null && categoryLst.size() != 0 ) {
			Type listType = new TypeToken<ArrayList<CategoryContentDto>>(){}.getType();
			List<CategoryContentDto> contentLst = (new Gson()).fromJson(categoryLst.get(0).getSurveyCategoryContent(), listType);  
			mav.addObject("surveyResultLst", contentLst);
		}

		mav.addObject(LinkType.NEXT_QUESTION.name(), LinkType.NEXT_QUESTION);
		mav.addObject(LinkType.SURVEY_RESULT.name(), LinkType.SURVEY_RESULT);
		
		// リファラ
		mav.addObject("referer", request.getHeader("referer"));
		mav.setViewName("/questionLinkUpdate");
		
		return mav;
	}
	
	private void convertEntityToQuestionLinkForm(SurveyQuestionLink link, QuestionLinkForm linkForm) {
		linkForm.setId(link.getId());
		linkForm.setSurveyManagementId(link.getSurveyManagementId());
		linkForm.setAnswerId(link.getAnswerId());
		linkForm.setQuestionId(link.getSurveyQuestionId());
		linkForm.setLinkType(link.getLinkType());
		linkForm.setLinkTo(link.getLinkTo());
	}

	@PostMapping("/surveyContentDetail/questionLinkUpdate/exec")
	public ModelAndView questionLinkUpdateExec(
			HttpServletRequest request,
			HttpSession session ,
			QuestionLinkUpdateForm questionLinkUpdateForm) throws Exception {
		ModelAndView mav = new ModelAndView();
		
		// セッションからユーザ情報取得
		User user = (User) session.getAttribute(CommonConstants.SESSION_KEY_USER_LOGIN);
		surveyContentService.getSurveyContentByIdAndUserId(questionLinkUpdateForm.getSurveyManagementId(), user.getId());
		
		List<SurveyQuestionLink> questionLinkLst = new ArrayList<SurveyQuestionLink>();
		if(questionLinkUpdateForm.getQuestionLinkLst() != null && questionLinkUpdateForm.getQuestionLinkLst().size() != 0 ) {
			this.convertQuestionLinkFormToEntity(questionLinkUpdateForm.getQuestionLinkLst(), questionLinkLst,questionLinkUpdateForm.getSurveyManagementId());
			surveyQuestionLinkService.surveyQuestionLinkLstUpdate(questionLinkLst);
		}
		
		mav.setViewName("redirect:/surveyContentList/contentDetail?contentId=" + questionLinkUpdateForm.getSurveyManagementId());
		
		return mav;
	}
	
	@GetMapping("/surveyContentDetail/questionFlowChart")
	public ModelAndView questionFlowChart(
			HttpServletRequest request,
			HttpSession session ,
			@RequestParam(value="contentId", required = true) Integer contentId,
			@RequestParam(value="onFlowchartFlg", required = false) boolean onFlowchartFlg) throws Exception {
		ModelAndView mav = new ModelAndView();
		// セッションからユーザ情報取得
		User user = (User) session.getAttribute(CommonConstants.SESSION_KEY_USER_LOGIN);
		SurveyManagement survey = surveyContentService.getSurveyContentByIdAndUserId(contentId, user.getId());
		mav.addObject("survey", survey);
		
		QuestionLinkUpdateForm questionLinkUpdateForm = new QuestionLinkUpdateForm();
		List<SurveyQuestion> questionLst = surveyQuestionService.getSurveyQuestionByContentIdOrderByOrderNo(contentId);
		List<QuestionContentUpdateForm> formLst = new ArrayList<QuestionContentUpdateForm>();
		if(questionLst != null && questionLst.size() != 0 ) {
			for(SurveyQuestion question : questionLst) {
				QuestionContentUpdateForm questionContentUpdateForm = new QuestionContentUpdateForm();
				this.convertEntityToQuestionForm(question, questionContentUpdateForm);
				formLst.add(questionContentUpdateForm);
			}
		}
		questionLinkUpdateForm.setQuestionFormLst(formLst);
		
		List<SurveyQuestionLink> questionLinkLst = surveyQuestionLinkService.getSurveyQuestionLinkLst(contentId);
		List<QuestionLinkForm> linkFormLst = new ArrayList<QuestionLinkForm>();
		if(questionLinkLst != null && questionLinkLst.size() != 0 ) {
			for(SurveyQuestionLink link : questionLinkLst) {
				QuestionLinkForm linkForm = new QuestionLinkForm();
				this.convertEntityToQuestionLinkForm(link, linkForm);
				linkFormLst.add(linkForm);
			}
		}
		questionLinkUpdateForm.setQuestionLinkLst(linkFormLst);
		questionLinkUpdateForm.setSurveyManagementId(contentId);
		
		SurveyCategory category = surveyCategoryService.getSurveyCategoryByContentId(contentId).get(0);
		
		SurveyCategoryUpdateForm surveyCategoryUpdateForm = new SurveyCategoryUpdateForm();
		this.convertEntityToCategoryForm(category, surveyCategoryUpdateForm, survey.getSurveyPatternId());
		
		// チャートデータ作成
		questionLinkUpdateForm.setFlowChartData(surveyQuestionLinkService.makeFlowchartData(formLst, linkFormLst, surveyCategoryUpdateForm));
		mav.addObject("questionLinkUpdateForm", questionLinkUpdateForm);
		
		// リファラ
		if(onFlowchartFlg && StringUtil.isNotEmpty((String)session.getAttribute(CommonConstants.SESSION_KEY_REFERER)) ) {
			mav.addObject("referer", (String)session.getAttribute(CommonConstants.SESSION_KEY_REFERER));
		}else {
			session.removeAttribute(CommonConstants.SESSION_KEY_REFERER);
			mav.addObject("referer", request.getHeader("referer"));
			session.setAttribute(CommonConstants.SESSION_KEY_REFERER, request.getHeader("referer"));
		}
		
		mav.setViewName("/questionFlowChart");
		
		return mav;
	}

	// Entityからフォームへ変換
	private void convertEntityToCategoryForm(SurveyCategory category, SurveyCategoryUpdateForm surveyCategoryUpdateForm, Integer patternId) {
		surveyCategoryUpdateForm.setId(category.getId());
		surveyCategoryUpdateForm.setSurveyManagementId(category.getSurveyManagementId());
		surveyCategoryUpdateForm.setSurveyCategoryName(category.getSurveyCategoryName());
		surveyCategoryUpdateForm.setSurveyCategoryColor(category.getSurveyCategoryColor());
		
		if( category.getSurveyCategoryPosition() != null ) {
			// 位置情報
			Type type = new TypeToken<QuestionPositionDto>(){}.getType();
			QuestionPositionDto resultPosition = (new Gson()).fromJson(category.getSurveyCategoryPosition(), type);  
			surveyCategoryUpdateForm.setSurveyCategoryPosition(resultPosition);
		}
		// カテゴリーコンテンツ
		Type listType = new TypeToken<ArrayList<CategoryContentDto>>(){}.getType();
		List<CategoryContentDto> contentLst = (new Gson()).fromJson(category.getSurveyCategoryContent(), listType);  
		surveyCategoryUpdateForm.setCategoryContentLst(contentLst);
	}
}
