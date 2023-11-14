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
import jp.co.SurveyMaker.Dto.AnswerContentDto;
import jp.co.SurveyMaker.Dto.CategoryContentDto;
import jp.co.SurveyMaker.Dto.SummaryResultDto;
import jp.co.SurveyMaker.Dto.SurveyCategoryResultDto;
import jp.co.SurveyMaker.Form.CategoryResultForm;
import jp.co.SurveyMaker.Form.QuestionContentUpdateForm;
import jp.co.SurveyMaker.Form.SurveyContentUpdateForm;
import jp.co.SurveyMaker.Form.SurveyResultForm;
import jp.co.SurveyMaker.Form.SurveySimulationForm;
import jp.co.SurveyMaker.Service.SurveyCategoryService;
import jp.co.SurveyMaker.Service.SurveyContentService;
import jp.co.SurveyMaker.Service.SurveyQuestionService;
import jp.co.SurveyMaker.Service.SurveySimulationService;
import jp.co.SurveyMaker.Service.Entity.SurveyCategory;
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
	
	@Value("${server.image.save.path}")
	 private String imgSavePath;
	
	@GetMapping("/surveyContentDetail/surveySimulation")
	public ModelAndView surveySimulation(
			HttpServletRequest request,
			HttpSession session ,
			@RequestParam(value="contentId", required = true) Integer contentId) throws Exception {
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
		
		if(survey.getSurveyPatternId() == CommonConstants.PARTTERN_SINGULAR) {
			// 単数
			mav.setViewName("/surveySimulationForSingular");
		}else if( survey.getSurveyPatternId() == CommonConstants.PARTTERN_COMPLEX_POINT || survey.getSurveyPatternId() == CommonConstants.PARTTERN_COMPLEX_TOTAL ) {
			// 複数
			mav.setViewName("/surveySimulationForComplex");
		}else {
			// フロー
			mav.setViewName("/surveySimulationForFlow");
		}
		
		mav.addObject("surveySimulationForm", simulationForm);
		return mav;
	}
	
	// 質問コンテンツフォーム変換
	private void convertEntityToQuestionLstForm(List<SurveyQuestion> questionLst,
			List<QuestionContentUpdateForm> questionFormLst) {
		if(questionLst != null && questionLst.size() !=0) {
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
	
	@PostMapping("/surveyContentDetail/surveySimulationForSingular/result")
	public ModelAndView surveySimulationForSingularResult(
			HttpServletRequest request,
			HttpSession session ,
			SurveySimulationForm surveySimulationForm) throws Exception {
		ModelAndView mav = new ModelAndView();
		
		// セッションからユーザ情報取得
		User user = (User) session.getAttribute(CommonConstants.SESSION_KEY_USER_LOGIN);
		SurveyManagement survey = surveyContentService.getSurveyContentByIdAndUserId(surveySimulationForm.getSurveyContent().getId(), user.getId());
		mav.addObject("survey", survey);
		
		// 診断結果作成及び登録
		Integer resultId = surveySimulationService.surveyResultRegist(surveySimulationForm.getAnswerResultLst());
		SurveyResult result = surveySimulationService.getSurveyResultById(resultId);
		
		// 診断結果画面フォーム設定
		SurveyResultForm surveyResultForm = new SurveyResultForm();
		surveyResultForm.setId(result.getId());
		surveyResultForm.setKey(result.getSurveyKey());
		surveyResultForm.setSurvey(this.convertSurveyContentEntityToForm(survey));
		// 総合評価とカテゴリー別の評価結果設定
		this.setSurveySummaryAndCategoryResult(surveyResultForm, result, survey.getSurveyPatternId());
		
		mav.addObject("surveyResultForm", surveyResultForm);
		mav.setViewName("/surveySimulationResultForSingular");
		
		return mav;
	}
	
	// 総合評価とカテゴリー別の評価結果設定
	private void setSurveySummaryAndCategoryResult(SurveyResultForm surveyResultForm, SurveyResult result, Integer patternId) throws Exception {
		// 一番ポイントのカテゴリーを設定
		Type type = new TypeToken<SummaryResultDto>(){}.getType();
		SummaryResultDto summaryResult = (new Gson()).fromJson(result.getSummaryResultContent(), type);  
		SurveyCategory topCategory = surveyCategoryService.getSurveyCategoryById(summaryResult.getTopCategoryId());
		surveyResultForm.setTopCategoryId(topCategory.getId());
		/*  総合評価 */
		if(!CommonConstants.PARTTERN_COMPLEX_TOTAL.equals(patternId) && !CommonConstants.PARTTERN_FLOW.equals(patternId)) {
			// Aboveの場合
			if( summaryResult.getTotalPoint() >= topCategory.getSurveySummaryDecidePoint() ) {
				surveyResultForm.setSurveySummaryTitle(topCategory.getSurveySummaryTitleAbove());
				surveyResultForm.setSurveySummaryDetail(topCategory.getSurveySummaryDetailAbove());
				// Above画像設定
				try {
					String imgFileName = topCategory.getSurveySummaryImageAbove();
					String imgFile = imgSavePath + FileUtil.FILE_DIRECTORY_DELIMITER + topCategory.getSurveyManagementId() + FileUtil.FILE_DIRECTORY_DELIMITER +CommonConstants.SAVA_IMG_PATH_CATEGORY + FileUtil.FILE_DIRECTORY_DELIMITER 
							+ topCategory.getId() +  FileUtil.FILE_DIRECTORY_DELIMITER + CommonConstants.SAVA_IMG_PATH_SUMMARY_ABOVE + FileUtil.FILE_DIRECTORY_DELIMITER 
							+ imgFileName;
					byte[] imgByte = Files.readAllBytes( new File(imgFile).toPath());
					String encodedImage = "data:image/" + imgFileName.substring(imgFileName.lastIndexOf(".") +1 ) + ";base64," 
							+ Base64.getEncoder().encodeToString(imgByte);
					surveyResultForm.setSurveySummaryImageBase64(encodedImage);
				} catch (IOException e) {
					log.error("総合評価画像ファイル取得にエラーが発生しました。",e);
				}
			}else { // Belowの場合
				surveyResultForm.setSurveySummaryTitle(topCategory.getSurveySummaryTitleBelow());
				surveyResultForm.setSurveySummaryDetail(topCategory.getSurveySummaryDetailBelow());
				// Below画像設定
				try {
					String imgFileName = topCategory.getSurveySummaryImageBelow();
					String imgFile = imgSavePath + FileUtil.FILE_DIRECTORY_DELIMITER + topCategory.getSurveyManagementId() + FileUtil.FILE_DIRECTORY_DELIMITER +CommonConstants.SAVA_IMG_PATH_CATEGORY + FileUtil.FILE_DIRECTORY_DELIMITER 
							+ topCategory.getId() +  FileUtil.FILE_DIRECTORY_DELIMITER + CommonConstants.SAVA_IMG_PATH_SUMMARY_BELOW + FileUtil.FILE_DIRECTORY_DELIMITER 
							+ imgFileName;
					byte[] imgByte = Files.readAllBytes( new File(imgFile).toPath());
					String encodedImage = "data:image/" + imgFileName.substring(imgFileName.lastIndexOf(".") +1 ) + ";base64," 
							+ Base64.getEncoder().encodeToString(imgByte);
					surveyResultForm.setSurveySummaryImageBase64(encodedImage);
				} catch (IOException e) {
					log.error("総合評価画像ファイル取得にエラーが発生しました。",e);
				}
			}
		}
		/*  カテゴリー別の評価結果リスト */
		Type listType = new TypeToken<ArrayList<SurveyCategoryResultDto>>(){}.getType();
		List<SurveyCategoryResultDto> categoryResultLst = (new Gson()).fromJson(result.getSurveyResultContent(), listType);
		List<CategoryResultForm> categoryResultFormLst = new ArrayList<CategoryResultForm>();
		for(SurveyCategoryResultDto categoryResult : categoryResultLst ) {
			CategoryResultForm resultForm  = new CategoryResultForm();
			SurveyCategory category = surveyCategoryService.getSurveyCategoryById(categoryResult.getCategoryId());
			resultForm.setCategoryId(category.getId());
			resultForm.setCategoryName(category.getSurveyCategoryName());
			resultForm.setCategoryPoint(categoryResult.getCategoryTotalPoint());
			resultForm.setCategoryResultId(categoryResult.getCategoryResultId());
			
			Type contentlistType = new TypeToken<ArrayList<CategoryContentDto>>(){}.getType();
			List<CategoryContentDto> contentLst = (new Gson()).fromJson(category.getSurveyCategoryContent(), contentlistType);  
			contentLst.forEach(content ->{
				if(content.getId() == categoryResult.getCategoryResultId() ) {
					resultForm.setCategoryResultTitle(content.getSurveyResult());
					resultForm.setCategoryResultDetail(content.getSurveyResultDetail());
					// 評価結果画像
					try {
						String imgFileName = content.getSurveyResultImage();
						String imgFile = imgSavePath + FileUtil.FILE_DIRECTORY_DELIMITER + category.getSurveyManagementId() + FileUtil.FILE_DIRECTORY_DELIMITER +CommonConstants.SAVA_IMG_PATH_CATEGORY + FileUtil.FILE_DIRECTORY_DELIMITER 
												+ category.getId() +  FileUtil.FILE_DIRECTORY_DELIMITER + content.getId() + FileUtil.FILE_DIRECTORY_DELIMITER + imgFileName;
						byte[] imgByte = Files.readAllBytes( new File(imgFile).toPath());
						String encodedImage = "data:image/" + imgFileName.substring(imgFileName.lastIndexOf(".") +1 ) + ";base64," 
								+ Base64.getEncoder().encodeToString(imgByte);
						resultForm.setCategoryResultImgBase64(encodedImage);
					} catch (IOException e) {
						log.error("診断軸の評価結果画像ファイル取得にエラーが発生しました。",e);
					}
				}
			});
			categoryResultFormLst.add(resultForm);
		}
		surveyResultForm.setCategoryResultLst(categoryResultFormLst);
	}

	// Entityからフォームへ設定
	private SurveyContentUpdateForm convertSurveyContentEntityToForm(SurveyManagement surveyContent) {
		SurveyContentUpdateForm surveyContentUpdateForm = new SurveyContentUpdateForm();
		surveyContentUpdateForm.setId(surveyContent.getId());
		surveyContentUpdateForm.setUserId(surveyContent.getUserId());
		surveyContentUpdateForm.setSurveyColor(surveyContent.getSurveyColor());

		try {
			String imgFileName = surveyContent.getSurveyImage();
			String imgFile = imgSavePath + FileUtil.FILE_DIRECTORY_DELIMITER + surveyContent.getId() + FileUtil.FILE_DIRECTORY_DELIMITER + imgFileName;
			byte[] imgByte = Files.readAllBytes( new File(imgFile).toPath());
			String encodedImage = "data:image/" + imgFileName.substring(imgFileName.lastIndexOf(".") +1 ) + ";base64," 
					+ Base64.getEncoder().encodeToString(imgByte);
			surveyContentUpdateForm.setSurveyImageBase64(encodedImage);
		} catch (IOException e) {
			log.error("コンテンツ画像ファイル取得にエラーが発生しました。",e);
		}
		
		surveyContentUpdateForm.setSurveyImage(surveyContent.getSurveyImage());
		surveyContentUpdateForm.setSurveyName(surveyContent.getSurveyName());
		surveyContentUpdateForm.setSurveyPatternId(surveyContent.getSurveyPatternId());
		return surveyContentUpdateForm;
	}

	@PostMapping("/surveyContentDetail/surveySimulationForComplex/result")
	public ModelAndView surveySimulationForComplexResult(
			HttpServletRequest request,
			HttpSession session ,
			SurveySimulationForm surveySimulationForm) throws Exception {
		ModelAndView mav = new ModelAndView();
		// セッションからユーザ情報取得
		User user = (User) session.getAttribute(CommonConstants.SESSION_KEY_USER_LOGIN);
		SurveyManagement survey = surveyContentService.getSurveyContentByIdAndUserId(surveySimulationForm.getSurveyContent().getId(), user.getId());
		mav.addObject("survey", survey);
		
		// 診断結果作成及び登録
		Integer resultId = surveySimulationService.surveyResultRegist(surveySimulationForm.getAnswerResultLst());
		SurveyResult result = surveySimulationService.getSurveyResultById(resultId);
		
		// 診断結果画面フォーム設定
		SurveyResultForm surveyResultForm = new SurveyResultForm();
		surveyResultForm.setId(result.getId());
		surveyResultForm.setKey(result.getSurveyKey());
		surveyResultForm.setSurvey(this.convertSurveyContentEntityToForm(survey));
		// 総合評価とカテゴリー別の評価結果設定
		this.setSurveySummaryAndCategoryResult(surveyResultForm, result, survey.getSurveyPatternId());
		
		mav.addObject("surveyResultForm", surveyResultForm);
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
