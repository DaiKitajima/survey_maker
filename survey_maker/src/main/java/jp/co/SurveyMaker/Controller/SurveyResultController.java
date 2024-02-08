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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jp.co.SurveyMaker.Constants.CommonConstants;
import jp.co.SurveyMaker.Dto.CategoryContentDto;
import jp.co.SurveyMaker.Dto.SummaryResultDto;
import jp.co.SurveyMaker.Dto.SurveyCategoryResultDto;
import jp.co.SurveyMaker.Form.CategoryResultForm;
import jp.co.SurveyMaker.Form.SurveyContentUpdateForm;
import jp.co.SurveyMaker.Form.SurveyResultForm;
import jp.co.SurveyMaker.Service.SurveyCategoryService;
import jp.co.SurveyMaker.Service.SurveyContentService;
import jp.co.SurveyMaker.Service.SurveyQuestionService;
import jp.co.SurveyMaker.Service.SurveySimulationService;
import jp.co.SurveyMaker.Service.Entity.SurveyCategory;
import jp.co.SurveyMaker.Service.Entity.SurveyManagement;
import jp.co.SurveyMaker.Service.Entity.SurveyResult;
import jp.co.SurveyMaker.Util.FileUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/api")
public class SurveyResultController {
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
	
	@GetMapping("/surveyResult")
	public ModelAndView surveyResult(
			HttpServletRequest request,
			HttpSession session ,
			@RequestParam(value="key", required = true) String key) throws Exception {
		ModelAndView mav = new ModelAndView();
		SurveyResult result = surveySimulationService.getSurveyResultByKey(key);
		
		SurveyManagement survey = surveyContentService.getSurveyContentById(result.getSurveyManagementId());
		SurveyContentUpdateForm surveyContentUpdateForm = new SurveyContentUpdateForm();
		this.convertSurveyContentEntityToForm(survey,surveyContentUpdateForm);
		mav.addObject("survey", surveyContentUpdateForm);
		
		// 診断結果画面フォーム設定
		SurveyResultForm surveyResultForm = new SurveyResultForm();
		surveyResultForm.setId(result.getId());
		surveyResultForm.setKey(result.getSurveyKey());
		surveyResultForm.setSurvey(surveyContentUpdateForm);
		// 総合評価とカテゴリー別の評価結果設定
		this.setSurveySummaryAndCategoryResult(surveyResultForm, result, survey.getSurveyPatternId());
		
		mav.addObject("surveyResultForm", surveyResultForm);
		
		if(CommonConstants.PARTTERN_SINGULAR.equals(survey.getSurveyPatternId())) {
			mav.setViewName("surveySimulationResultForSingular");
		}else if(CommonConstants.PARTTERN_FLOW.equals(survey.getSurveyPatternId())) {
			mav.setViewName("surveySimulationResultForFlow");
		}else {
			mav.setViewName("surveySimulationResultForComplex");
		}
		
		return mav;
	}
	
	// 診断コンテンツをフォームに詰める
	private void convertSurveyContentEntityToForm(SurveyManagement surveyContent,
			SurveyContentUpdateForm surveyContentUpdateForm) {
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
		
		try {
			String imgFileName = surveyContent.getSurveyHeaderImage();
			String imgFile = imgSavePath + FileUtil.FILE_DIRECTORY_DELIMITER + surveyContent.getId() + FileUtil.FILE_DIRECTORY_DELIMITER + imgFileName;
			byte[] imgByte = Files.readAllBytes( new File(imgFile).toPath());
			String encodedImage = "data:image/" + imgFileName.substring(imgFileName.lastIndexOf(".") +1 ) + ";base64," 
					+ Base64.getEncoder().encodeToString(imgByte);
			surveyContentUpdateForm.setSurveyHeaderImageBase64(encodedImage);
		} catch (IOException e) {
			log.error("コンテンツヘッダ画像ファイル取得にエラーが発生しました。",e);
		}
		surveyContentUpdateForm.setSurveyHeaderImage(surveyContent.getSurveyHeaderImage());
		surveyContentUpdateForm.setSurveyImage(surveyContent.getSurveyImage());
		surveyContentUpdateForm.setSurveyDescription(surveyContent.getSurveyDescription());
		surveyContentUpdateForm.setSurveyInduceArea(surveyContent.getSurveyInduceArea());
		surveyContentUpdateForm.setSummaryDisplayFlg(surveyContent.getSummaryDisplayFlg());
		surveyContentUpdateForm.setSurveyName(surveyContent.getSurveyName());
		surveyContentUpdateForm.setSurveyPatternId(surveyContent.getSurveyPatternId());
	}
	
	// 総合評価とカテゴリー別の評価結果設定
	private void setSurveySummaryAndCategoryResult(SurveyResultForm surveyResultForm, SurveyResult result , Integer patternId) throws Exception {
		// 一番ポイントのカテゴリーを設定
		if(!CommonConstants.PARTTERN_FLOW.equals(patternId)) {
			Type type = new TypeToken<SummaryResultDto>(){}.getType();
			SummaryResultDto summaryResult = (new Gson()).fromJson(result.getSummaryResultContent(), type);  
			SurveyCategory topCategory = surveyCategoryService.getSurveyCategoryById(summaryResult.getTopCategoryId());
			surveyResultForm.setTopCategoryId(topCategory.getId());
		}
		
		/*  総合評価 */
		if(!CommonConstants.PARTTERN_COMPLEX_TOTAL.equals(patternId) && !CommonConstants.PARTTERN_FLOW.equals(patternId)) {
			Type type = new TypeToken<SummaryResultDto>(){}.getType();
			SummaryResultDto summaryResult = (new Gson()).fromJson(result.getSummaryResultContent(), type);  
			SurveyCategory topCategory = surveyCategoryService.getSurveyCategoryById(summaryResult.getTopCategoryId());
			// Aboveの場合
			if( summaryResult.getTotalPoint() >= topCategory.getSurveySummaryDecidePoint() ) {
				surveyResultForm.setSurveySummaryTitle(topCategory.getSurveySummaryTitleAbove());
				surveyResultForm.setSurveySummaryDetail(topCategory.getSurveySummaryDetailAbove());
				surveyResultForm.setSummaryInduceArea(topCategory.getSurveySummaryInduceAbove());
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
				surveyResultForm.setSummaryInduceArea(topCategory.getSurveySummaryInduceBelow());
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
			resultForm.setCategoryColor(category.getSurveyCategoryColor());
			resultForm.setCategoryPoint(categoryResult.getCategoryTotalPoint());
			resultForm.setCategoryMaxPoint(categoryResult.getCategoryMaxPoint());
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
}
