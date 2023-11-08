package jp.co.SurveyMaker.Controller;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import jp.co.SurveyMaker.Dto.AnswerContentDto;
import jp.co.SurveyMaker.Dto.QuestionOrderDto;
import jp.co.SurveyMaker.Form.QuestionContentUpdateForm;
import jp.co.SurveyMaker.Service.SurveyCategoryService;
import jp.co.SurveyMaker.Service.SurveyContentService;
import jp.co.SurveyMaker.Service.SurveyQuestionLinkService;
import jp.co.SurveyMaker.Service.SurveyQuestionService;
import jp.co.SurveyMaker.Service.Entity.SurveyManagement;
import jp.co.SurveyMaker.Service.Entity.SurveyQuestion;
import jp.co.SurveyMaker.Service.Entity.User;
import jp.co.SurveyMaker.Util.FileUtil;
import jp.co.SurveyMaker.Util.StringUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class SurveyQuestionController {
	@Autowired
	private SurveyCategoryService surveyCategoryService;
	
	@Autowired
	private SurveyQuestionService surveyQuestionService;
	
	@Autowired
	private SurveyQuestionLinkService surveyQuestionLinkService;
	
	@Autowired
	private SurveyContentService surveyContentService;
	
	 @Value("${server.image.save.path}")
	 private String imgSavePath;
	 
	@GetMapping("/surveyContentDetail/questionContentRegist")
	public ModelAndView questionContentRegist(
			HttpServletRequest request,
			HttpSession session ,
			@RequestParam(value="contentId", required = true, defaultValue="-1") Integer contentId) throws Exception {
		ModelAndView mav = new ModelAndView();
		// セッションからユーザ情報取得
		User user = (User) session.getAttribute(CommonConstants.SESSION_KEY_USER_LOGIN);
		// コンテンツ情報取得
		SurveyManagement survey = surveyContentService.getSurveyContentByIdAndUserId(contentId, user.getId());
		mav.addObject("survey", survey);
		
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
		// セッションからユーザ情報取得
		User user = (User) session.getAttribute(CommonConstants.SESSION_KEY_USER_LOGIN);
		// ユーザ所属のコンテンツか検証
		surveyContentService.getSurveyContentByIdAndUserId(questionContentUpdateForm.getSurveyManagementId(), user.getId());
		
		SurveyQuestion questionContent = new SurveyQuestion();
		this.convertQuestionFormToEntity(questionContentUpdateForm, questionContent);
		Integer questionId = surveyQuestionService.surveyQuestionRegist(questionContent);
		
		String savePath = imgSavePath + FileUtil.FILE_DIRECTORY_DELIMITER + questionContentUpdateForm.getSurveyManagementId() + FileUtil.FILE_DIRECTORY_DELIMITER +CommonConstants.SAVA_IMG_PATH_QUESTION
										+ FileUtil.FILE_DIRECTORY_DELIMITER + questionId + FileUtil.FILE_DIRECTORY_DELIMITER;
		FileUtil.registTargetFile(savePath, CommonConstants.SAVA_IMG_NAME_QUESTION + questionContentUpdateForm.getQuestionOrderNo() , questionContentUpdateForm.getQuestionImgFile());

		mav.setViewName("redirect:/surveyContentList/contentDetail?contentId=" + questionContentUpdateForm.getSurveyManagementId());
		
		return mav;
	}
	
	private void convertQuestionFormToEntity(QuestionContentUpdateForm questionContentUpdateForm,
			SurveyQuestion questionContent) throws Exception {
		questionContent.setId(questionContentUpdateForm.getId());
		questionContent.setSurveyManagementId(questionContentUpdateForm.getSurveyManagementId());	
		questionContent.setQuestionTitle(questionContentUpdateForm.getQuestionTitle());
		questionContent.setQuestionOrderNo(questionContentUpdateForm.getQuestionOrderNo());
		// 質問画像
		if(questionContentUpdateForm.getQuestionImgFile() != null && StringUtil.isNotEmpty(questionContentUpdateForm.getQuestionImgFile().getOriginalFilename()) ) {
			String uploadFileName = questionContentUpdateForm.getQuestionImgFile().getOriginalFilename();
			String externalKey = uploadFileName.substring(uploadFileName.lastIndexOf("."));
			questionContent.setQuestionImage(CommonConstants.SAVA_IMG_NAME_QUESTION+ questionContentUpdateForm.getQuestionOrderNo() + externalKey);
			
		}else {
			questionContent.setQuestionImage(questionContentUpdateForm.getQuestionImage());
		}
		
		// 回答コンテンツ
		if(questionContentUpdateForm.getAnswerContentLst() != null && questionContentUpdateForm.getAnswerContentLst().size() != 0 ) {
			// 回答ID取得
			Integer answerContentId ;
			// 新規登録の場合
			if(questionContentUpdateForm.getId() == null) {
				answerContentId = 1;
			}else { // 更新の場合
				SurveyQuestion existedQuestion = surveyQuestionService.getSurveyQuestionById(questionContentUpdateForm.getId() );
				Type listType = new TypeToken<ArrayList<AnswerContentDto>>(){}.getType();
				List<AnswerContentDto> existedContentLst = (new Gson()).fromJson(existedQuestion.getAnswerContent(), listType);
				
				Optional<AnswerContentDto> maxOptional = existedContentLst.stream()
																									.max(Comparator.comparing(AnswerContentDto::getAnswerId, Comparator.nullsFirst(Integer::compareTo)));
				if (maxOptional.isPresent()) {
					answerContentId = maxOptional.get().getAnswerId() != null ? maxOptional.get().getAnswerId() +1: 1 ;
				} else {
					answerContentId = 1;
				}
			}
				
			for(AnswerContentDto answer : questionContentUpdateForm.getAnswerContentLst() ) {
				// 評価コンテンツID設定
				if(answer.getAnswerId() == null) {
					answer.setAnswerId(answerContentId);
					answerContentId++;
				}
			}
		}
		questionContent.setAnswerContent(new Gson().toJson(questionContentUpdateForm.getAnswerContentLst() ));
	}

	@GetMapping("/surveyContentDetail/questionContentUpdate")
	public ModelAndView questionContentUpdate(
			HttpServletRequest request,
			HttpSession session ,
			@RequestParam(value="questionId", required = true, defaultValue="-1") Integer questionId) throws Exception {
		ModelAndView mav = new ModelAndView();
		// セッションからユーザ情報取得
		User user = (User) session.getAttribute(CommonConstants.SESSION_KEY_USER_LOGIN);
		 SurveyQuestion question = surveyQuestionService.getSurveyQuestionById(questionId);
		SurveyManagement survey = surveyContentService.getSurveyContentByIdAndUserId(question.getSurveyManagementId(), user.getId());
		mav.addObject("survey", survey);
		
		QuestionContentUpdateForm  questionContentUpdateForm = new QuestionContentUpdateForm();
		this.convertEntityToQuestionForm(question, questionContentUpdateForm);
		
		mav.addObject("questionContentUpdateForm", questionContentUpdateForm);
		mav.addObject("categoryLst", surveyCategoryService.getSurveyCategoryByContentId(question.getSurveyManagementId()));
		
		mav.setViewName("/questionContentUpdate");
		
		return mav;
	}
	
	private void convertEntityToQuestionForm(SurveyQuestion question,
			QuestionContentUpdateForm questionContentUpdateForm) {
		questionContentUpdateForm.setId(question.getId());
		questionContentUpdateForm.setSurveyManagementId(question.getSurveyManagementId());	
		questionContentUpdateForm.setQuestionTitle(question.getQuestionTitle());
		questionContentUpdateForm.setQuestionOrderNo(question.getQuestionOrderNo());
		questionContentUpdateForm.setQuestionImage(question.getQuestionImage());
		// 質問画像
		try {
			String imgFileName = question.getQuestionImage();
			String imgFile = imgSavePath + FileUtil.FILE_DIRECTORY_DELIMITER + question.getSurveyManagementId() + FileUtil.FILE_DIRECTORY_DELIMITER +CommonConstants.SAVA_IMG_PATH_QUESTION + FileUtil.FILE_DIRECTORY_DELIMITER 
									+ question.getId() +  FileUtil.FILE_DIRECTORY_DELIMITER +  imgFileName;
			byte[] imgByte = Files.readAllBytes( new File(imgFile).toPath());
			String encodedImage = "data:image/" + imgFileName.substring(imgFileName.lastIndexOf(".") +1 ) + ";base64," 
					+ Base64.getEncoder().encodeToString(imgByte);
			questionContentUpdateForm.setQuestionImageBase64(encodedImage);
		} catch (IOException e) {
			log.error("質問画像ファイル取得にエラーが発生しました。",e);
		}
		
		// 回答コンテンツ
		Type listType = new TypeToken<ArrayList<AnswerContentDto>>(){}.getType();
		List<AnswerContentDto> answerContentLst = (new Gson()).fromJson(question.getAnswerContent(), listType);  
		questionContentUpdateForm.setAnswerContentLst(answerContentLst);
	}

	@PostMapping("/surveyContentDetail/questionContentUpdate/exec")
	public ModelAndView questionContentUpdateExec(
			HttpServletRequest request,
			HttpSession session ,
			QuestionContentUpdateForm questionContentUpdateForm) throws Exception {
		ModelAndView mav = new ModelAndView();
		
		// セッションからユーザ情報取得
		User user = (User) session.getAttribute(CommonConstants.SESSION_KEY_USER_LOGIN);
		// ユーザ所属のコンテンツか検証
		surveyContentService.getSurveyContentByIdAndUserId(questionContentUpdateForm.getSurveyManagementId(), user.getId());
		
		SurveyQuestion questionContent = new SurveyQuestion();
		this.convertQuestionFormToEntity(questionContentUpdateForm, questionContent);
		surveyQuestionService.surveyQuestionUpdate(questionContent);
		
		if(questionContentUpdateForm.getQuestionImgFile() != null && StringUtil.isNotEmpty(questionContentUpdateForm.getQuestionImgFile().getOriginalFilename())) {
			String savePath = imgSavePath + FileUtil.FILE_DIRECTORY_DELIMITER + questionContentUpdateForm.getSurveyManagementId() + FileUtil.FILE_DIRECTORY_DELIMITER +CommonConstants.SAVA_IMG_PATH_QUESTION
					+ FileUtil.FILE_DIRECTORY_DELIMITER + questionContent.getId() + FileUtil.FILE_DIRECTORY_DELIMITER;
			FileUtil.registTargetFile(savePath, CommonConstants.SAVA_IMG_NAME_QUESTION + questionContentUpdateForm.getQuestionOrderNo() , questionContentUpdateForm.getQuestionImgFile());
		}
		
		mav.setViewName("redirect:/surveyContentList/contentDetail?contentId=" + questionContentUpdateForm.getSurveyManagementId());
		return mav;
	}
	
	@GetMapping("/surveyContentDetail/questionContentDelete")
	public ModelAndView questionContentDelete(
			HttpServletRequest request,
			HttpSession session ,
			@RequestParam(value="questionIdLst", required = true) List<Integer> questionIdLst) throws Exception {
		ModelAndView mav = new ModelAndView();
		Integer contentId=-1;
		// セッションからユーザ情報取得
		User user = (User) session.getAttribute(CommonConstants.SESSION_KEY_USER_LOGIN);
		if(questionIdLst != null && questionIdLst.size() != 0 ) {
			for(Integer questionId : questionIdLst) {
				try {
					SurveyQuestion question = surveyQuestionService.getSurveyQuestionById(questionId);
					contentId = question.getSurveyManagementId();
					// ユーザ所属のコンテンツか検証
					surveyContentService.getSurveyContentByIdAndUserId(question.getSurveyManagementId(), user.getId());
					// 質問コンテンツ削除
					surveyQuestionService.surveyQuestionDelete(questionId);
					// 質問リンク削除
					surveyQuestionLinkService.deleteQuestionLinkByContentIdAndQuestionId(question.getSurveyManagementId(), questionId);
				} catch (Exception e) {
					log.error("質問コンテンツ削除にエラーが発生しました。 ", e );
					continue;
				}
			}
		}
		
		mav.setViewName("redirect:/surveyContentList/contentDetail?contentId="+ contentId);
		return mav;
	}
	
	@PostMapping("/surveyContentDetail/questionOrderUpdate")
	public ModelAndView questionOrderUpdate(
			HttpServletRequest request,
			HttpSession session ,
			Integer contentId,
			String orderJson) throws Exception {
		ModelAndView mav = new ModelAndView();
		
		// セッションからユーザ情報取得
		User user = (User) session.getAttribute(CommonConstants.SESSION_KEY_USER_LOGIN);
		// ユーザ所属のコンテンツか検証
		surveyContentService.getSurveyContentByIdAndUserId(contentId, user.getId());
		
		Type listType = new TypeToken<ArrayList<QuestionOrderDto>>(){}.getType();
		List<QuestionOrderDto> questionOrderLst = (new Gson()).fromJson(orderJson, listType);  
		
		// 質問順番更新
		surveyQuestionService.questionOrderUpdate(questionOrderLst);
		
		mav.setViewName("redirect:/surveyContentList/contentDetail?contentId="+ contentId);
		return mav;
	}
}
