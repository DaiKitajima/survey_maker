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
import jp.co.SurveyMaker.Form.QuestionContentUpdateForm;
import jp.co.SurveyMaker.Form.QuestionLinkUpdateForm;
import jp.co.SurveyMaker.Service.SurveyCategoryService;
import jp.co.SurveyMaker.Service.SurveyContentService;
import jp.co.SurveyMaker.Service.SurveyQuestionService;
import jp.co.SurveyMaker.Service.Entity.SurveyManagement;
import jp.co.SurveyMaker.Service.Entity.SurveyQuestion;
import jp.co.SurveyMaker.Service.Entity.User;
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
		Integer answerTotalCnt = 0;
		if(questionLst != null && questionLst.size() != 0 ) {
			for(SurveyQuestion question : questionLst) {
				QuestionContentUpdateForm questionContentUpdateForm = new QuestionContentUpdateForm();
				this.convertEntityToQuestionForm(question, questionContentUpdateForm);
				
				if(questionContentUpdateForm.getAnswerContentLst() != null ) {
					answerTotalCnt = answerTotalCnt + questionContentUpdateForm.getAnswerContentLst().size();
				}
				formLst.add(questionContentUpdateForm);
			}
		}
		questionLinkUpdateForm.setQuestionFormLst(formLst);
		questionLinkUpdateForm.setId(null);
		questionLinkUpdateForm.setSurveyManagementId(contentId);
		mav.addObject("questionLinkUpdateForm", questionLinkUpdateForm);
		mav.addObject("answerTotalCnt", answerTotalCnt);
		
		mav.addObject("linkTypeMap", Stream.of(LinkType.values()).collect(Collectors.toMap(t->t.getCode(),  t->t.getDisplay())));
		
		// カテゴリー評価結果コンテンツ
		Type listType = new TypeToken<ArrayList<CategoryContentDto>>(){}.getType();
		List<CategoryContentDto> contentLst = (new Gson()).fromJson(surveyCategoryService.getSurveyCategoryByContentId(contentId).get(0).getSurveyCategoryContent(), listType);  
		mav.addObject("surveyResultLst", contentLst);
		
		
		
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
		
		mav.addObject("questionLinkUpdateForm", new QuestionLinkUpdateForm());
		mav.setViewName("redirect:/surveyContentList/contentDetail?id=1");
		
		return mav;
	}
	
	@GetMapping("/surveyContentDetail/questionLinkUpdate")
	public ModelAndView questionLinkUpdate(
			HttpServletRequest request,
			HttpSession session ,
			@RequestParam(value="question", required = true, defaultValue="-1") Integer question) throws Exception {
		ModelAndView mav = new ModelAndView();
		
		mav.addObject("questionLinkUpdateForm", new QuestionLinkUpdateForm());
		mav.setViewName("/questionLinkUpdate");
		
		return mav;
	}
	
	@PostMapping("/surveyContentDetail/questionLinkUpdate/exec")
	public ModelAndView questionLinkUpdateExec(
			HttpServletRequest request,
			HttpSession session ,
			QuestionLinkUpdateForm questionLinkUpdateForm) throws Exception {
		ModelAndView mav = new ModelAndView();
		
		mav.addObject("questionLinkUpdateForm", new QuestionLinkUpdateForm());
		mav.setViewName("redirect:/surveyContentList/contentDetail?id=1");
		
		return mav;
	}
}
