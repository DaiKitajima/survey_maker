package jp.co.SurveyMaker.Controller;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

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
import jp.co.SurveyMaker.Constants.LinkType;
import jp.co.SurveyMaker.Dto.AnswerContentDto;
import jp.co.SurveyMaker.Form.QuestionContentUpdateForm;
import jp.co.SurveyMaker.Form.SurveySimulationForm;
import jp.co.SurveyMaker.Service.SurveyCategoryService;
import jp.co.SurveyMaker.Service.SurveyContentService;
import jp.co.SurveyMaker.Service.SurveyQuestionLinkService;
import jp.co.SurveyMaker.Service.SurveyQuestionService;
import jp.co.SurveyMaker.Service.SurveySimulationService;
import jp.co.SurveyMaker.Service.Entity.SurveyManagement;
import jp.co.SurveyMaker.Service.Entity.SurveyQuestion;
import jp.co.SurveyMaker.Service.Entity.SurveyResult;
import jp.co.SurveyMaker.Service.Entity.User;
import jp.co.SurveyMaker.Util.FileUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class SurveySimulationController {
	@Autowired
	private SurveyContentService surveyContentService;

	@Autowired
	private SurveyQuestionService surveyQuestionService;

	@Autowired
	private SurveySimulationService surveySimulationService;

	@Autowired
	private SurveyCategoryService surveyCategoryService;

	@Autowired
	private SurveyQuestionLinkService surveyQuestionLinkService;

	@Value("${server.image.save.path}")
	private String imgSavePath;

	@GetMapping("/surveyContentDetail/surveySimulation")
	public ModelAndView surveySimulation(HttpServletRequest request, HttpSession session,
			@RequestParam(value = "contentId", required = true) Integer contentId) throws Exception {
		ModelAndView mav = new ModelAndView();
		// セッションからユーザ情報取得
		User user = (User) session.getAttribute(CommonConstants.SESSION_KEY_USER_LOGIN);
		SurveyManagement survey = surveyContentService.getSurveyContentByIdAndUserId(contentId, user.getId());
		mav.addObject("survey", survey);

		SurveySimulationForm simulationForm = new SurveySimulationForm();
		simulationForm.setSurveyContent(survey);
		// 質問コンテンツフォーム設定
		List<SurveyQuestion> questionLst = surveyQuestionService.getSurveyQuestionByContentIdOrderByOrderNo(contentId);
		List<QuestionContentUpdateForm> questionFormLst = new ArrayList<QuestionContentUpdateForm>();
		this.convertEntityToQuestionLstForm(questionLst, questionFormLst);
		simulationForm.setQuestionFormLst(questionFormLst);

		if (survey.getSurveyPatternId() == CommonConstants.PARTTERN_SINGULAR) {
			// 単数
			mav.setViewName("/surveySimulationForSingular");
		} else if (survey.getSurveyPatternId() == CommonConstants.PARTTERN_COMPLEX_POINT
				|| survey.getSurveyPatternId() == CommonConstants.PARTTERN_COMPLEX_TOTAL) {
			// 複数
			mav.setViewName("/surveySimulationForComplex");
		} else { // フロー
			// 質問リンクコンテンツフォーム設定
			simulationForm.setQuestionLinkLst(surveyQuestionLinkService.getSurveyQuestionLinkLst(contentId));
			mav.addObject(LinkType.NEXT_QUESTION.name(), LinkType.NEXT_QUESTION);
			mav.addObject(LinkType.SURVEY_RESULT.name(), LinkType.SURVEY_RESULT);

			mav.setViewName("/surveySimulationForFlow");
		}

		mav.addObject("surveySimulationForm", simulationForm);
		return mav;
	}

	// 質問コンテンツフォーム変換
	private void convertEntityToQuestionLstForm(List<SurveyQuestion> questionLst,
			List<QuestionContentUpdateForm> questionFormLst) {
		if (questionLst != null && questionLst.size() != 0) {
			questionLst.forEach(que -> {
				QuestionContentUpdateForm form = new QuestionContentUpdateForm();
				this.convertEntityToQuestionForm(que, form);
				questionFormLst.add(form);
			});
		}
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
			String imgFile = imgSavePath + FileUtil.FILE_DIRECTORY_DELIMITER + question.getSurveyManagementId()
					+ FileUtil.FILE_DIRECTORY_DELIMITER + CommonConstants.SAVA_IMG_PATH_QUESTION
					+ FileUtil.FILE_DIRECTORY_DELIMITER + question.getId() + FileUtil.FILE_DIRECTORY_DELIMITER
					+ imgFileName;
			byte[] imgByte = Files.readAllBytes(new File(imgFile).toPath());
			String encodedImage = "data:image/" + imgFileName.substring(imgFileName.lastIndexOf(".") + 1) + ";base64,"
					+ Base64.getEncoder().encodeToString(imgByte);
			questionContentUpdateForm.setQuestionImageBase64(encodedImage);
		} catch (IOException e) {
			log.error("質問画像ファイル取得にエラーが発生しました。", e);
		}

		// 回答コンテンツ
		Type listType = new TypeToken<ArrayList<AnswerContentDto>>() {
		}.getType();
		List<AnswerContentDto> answerContentLst = (new Gson()).fromJson(question.getAnswerContent(), listType);
		questionContentUpdateForm.setAnswerContentLst(answerContentLst);
	}

	@PostMapping("/surveyContentDetail/surveySimulationForSingular/result")
	public ModelAndView surveySimulationForSingularResult(HttpServletRequest request, HttpSession session,
			SurveySimulationForm surveySimulationForm) throws Exception {
		ModelAndView mav = new ModelAndView();

		// セッションからユーザ情報取得
		User user = (User) session.getAttribute(CommonConstants.SESSION_KEY_USER_LOGIN);
		SurveyManagement survey = surveyContentService
				.getSurveyContentByIdAndUserId(surveySimulationForm.getSurveyContent().getId(), user.getId());
		mav.addObject("survey", survey);

		// 診断結果作成及び登録
		Integer resultId = surveySimulationService.surveyResultRegist(surveySimulationForm.getAnswerResultLst());
		SurveyResult result = surveySimulationService.getSurveyResultById(resultId);

		mav.setViewName("redirect:/api/surveyResult?key=" + result.getSurveyKey());
		return mav;
	}

	@PostMapping("/surveyContentDetail/surveySimulationForComplex/result")
	public ModelAndView surveySimulationForComplexResult(HttpServletRequest request, HttpSession session,
			SurveySimulationForm surveySimulationForm) throws Exception {
		ModelAndView mav = new ModelAndView();
		// セッションからユーザ情報取得
		User user = (User) session.getAttribute(CommonConstants.SESSION_KEY_USER_LOGIN);
		SurveyManagement survey = surveyContentService
				.getSurveyContentByIdAndUserId(surveySimulationForm.getSurveyContent().getId(), user.getId());
		mav.addObject("survey", survey);

		// 診断結果作成及び登録
		Integer resultId = surveySimulationService.surveyResultRegist(surveySimulationForm.getAnswerResultLst());
		SurveyResult result = surveySimulationService.getSurveyResultById(resultId);

		mav.setViewName("redirect:/api/surveyResult?key=" + result.getSurveyKey());
		return mav;
	}

	@GetMapping("/surveyContentDetail/surveySimulationForFlow/result")
	public ModelAndView surveySimulationForFlowResult(HttpServletRequest request, HttpSession session,
			@RequestParam(value = "contentId", required = true) Integer contentId,
			@RequestParam(value = "linkTo", required = true) Integer linkTo) throws Exception {
		ModelAndView mav = new ModelAndView();

		// セッションからユーザ情報取得
		User user = (User) session.getAttribute(CommonConstants.SESSION_KEY_USER_LOGIN);
		SurveyManagement survey = surveyContentService.getSurveyContentByIdAndUserId(contentId, user.getId());
		mav.addObject("survey", survey);

		// 診断結果作成及び登録
		Integer resultId = surveySimulationService.surveyResultRegistForFlow(contentId, linkTo);
		SurveyResult result = surveySimulationService.getSurveyResultById(resultId);

		mav.setViewName("redirect:/api/surveyResult?key=" + result.getSurveyKey());
		return mav;
	}
}
