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
import jp.co.SurveyMaker.Dto.CategoryContentDto;
import jp.co.SurveyMaker.Form.SurveyCategoryUpdateForm;
import jp.co.SurveyMaker.Service.SurveyCategoryService;
import jp.co.SurveyMaker.Service.SurveyContentService;
import jp.co.SurveyMaker.Service.Entity.SurveyCategory;
import jp.co.SurveyMaker.Service.Entity.SurveyManagement;
import jp.co.SurveyMaker.Service.Entity.User;
import jp.co.SurveyMaker.Util.FileUtil;
import jp.co.SurveyMaker.Util.StringUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class SurveyCategoryController {
	@Autowired
	private SurveyContentService surveyContentService;
	
	@Autowired
	private SurveyCategoryService surveyCategoryService;
	
	 @Value("${server.image.save.path}")
	 private String imgSavePath;
	 
	@GetMapping("/surveyContentDetail/surveyCategoryRegist")
	public ModelAndView surveyCategoryRegist(
			HttpServletRequest request,
			HttpSession session ,
			@RequestParam(value="contentId", required = true, defaultValue="-1") Integer contentId) throws Exception {
		ModelAndView mav = new ModelAndView();
		// セッションからユーザ情報取得
		User user = (User) session.getAttribute(CommonConstants.SESSION_KEY_USER_LOGIN);
		
		SurveyManagement survey = surveyContentService.getSurveyContentByIdAndUserId(contentId, user.getId());
		mav.addObject("survey", survey);
		
		SurveyCategoryUpdateForm surveyCategoryUpdateForm = new SurveyCategoryUpdateForm();
		surveyCategoryUpdateForm.setSurveyManagementId(contentId);
		
		mav.addObject("surveyCategoryUpdateForm", surveyCategoryUpdateForm);
		// リファラ
		mav.addObject("referer", request.getHeader("referer"));
		mav.setViewName("surveyCategoryRegist");
		
		return mav;
	}
	
	@PostMapping("/surveyContentDetail/surveyCategoryRegist/exec")
	public ModelAndView surveyCategoryRegistExec(
			HttpServletRequest request,
			HttpSession session ,
			SurveyCategoryUpdateForm surveyCategoryUpdateForm) throws Exception {
		ModelAndView mav = new ModelAndView();
		
		User user = (User) session.getAttribute(CommonConstants.SESSION_KEY_USER_LOGIN);
		surveyContentService.getSurveyContentByIdAndUserId(surveyCategoryUpdateForm.getSurveyManagementId(), user.getId());
		
		// カテゴリーデータ登録
		SurveyCategory category = new SurveyCategory();
		this.convertCategoryFormToEntity(surveyCategoryUpdateForm, category);
		Integer categoryId = surveyCategoryService.surveyCategoryRegist(category);
		
		// カテゴリー関連画像登録
		this.surveyCategoryImageRegist(categoryId, surveyCategoryUpdateForm, category);
		
		mav.setViewName("redirect:/surveyContentList/contentDetail?contentId=" + surveyCategoryUpdateForm.getSurveyManagementId());
		
		return mav;
	}
	
	// カテゴリー関連画像の登録
	private void surveyCategoryImageRegist(Integer categoryId, SurveyCategoryUpdateForm surveyCategoryUpdateForm,
			SurveyCategory category) throws Exception{
		String savePath = "";
		// 総合評価画像(判定点数以上)保存
		savePath = imgSavePath + FileUtil.FILE_DIRECTORY_DELIMITER + surveyCategoryUpdateForm.getSurveyManagementId() + FileUtil.FILE_DIRECTORY_DELIMITER +CommonConstants.SAVA_IMG_PATH_CATEGORY
						+ FileUtil.FILE_DIRECTORY_DELIMITER + categoryId + FileUtil.FILE_DIRECTORY_DELIMITER + CommonConstants.SAVA_IMG_PATH_SUMMARY_ABOVE + FileUtil.FILE_DIRECTORY_DELIMITER;
		if(surveyCategoryUpdateForm.getSurveySummaryImageAboveFile() != null ) {
			FileUtil.registTargetFile(savePath, surveyCategoryUpdateForm.getSurveySummaryTitleAbove(), surveyCategoryUpdateForm.getSurveySummaryImageAboveFile());
		}
		// 総合評価画像(判定点数以下)保存
		savePath = imgSavePath + FileUtil.FILE_DIRECTORY_DELIMITER + surveyCategoryUpdateForm.getSurveyManagementId() + FileUtil.FILE_DIRECTORY_DELIMITER +CommonConstants.SAVA_IMG_PATH_CATEGORY
						+ FileUtil.FILE_DIRECTORY_DELIMITER + categoryId + FileUtil.FILE_DIRECTORY_DELIMITER + CommonConstants.SAVA_IMG_PATH_SUMMARY_BELOW + FileUtil.FILE_DIRECTORY_DELIMITER;
		if(surveyCategoryUpdateForm.getSurveySummaryImageBelowFile() != null ) {
			FileUtil.registTargetFile(savePath, surveyCategoryUpdateForm.getSurveySummaryTitleBelow(), surveyCategoryUpdateForm.getSurveySummaryImageBelowFile());
		}
		// 評価結果画像保存
		if(surveyCategoryUpdateForm.getCategoryContentLst() != null && surveyCategoryUpdateForm.getCategoryContentLst().size() != 0  ) {
			Type listType = new TypeToken<ArrayList<CategoryContentDto>>(){}.getType();
			List<CategoryContentDto> contentLst = (new Gson()).fromJson(category.getSurveyCategoryContent(), listType);  

			for(CategoryContentDto content : surveyCategoryUpdateForm.getCategoryContentLst()) {
				String surveyResultImgPath = "";
				Integer surveyResultId = contentLst.stream().filter(d -> d.getSurveyResult().equals(content.getSurveyResult())).toList().get(0).getId();
				if(content.getSurveyResultImageFile() != null) {
					surveyResultImgPath = imgSavePath + FileUtil.FILE_DIRECTORY_DELIMITER + surveyCategoryUpdateForm.getSurveyManagementId() + FileUtil.FILE_DIRECTORY_DELIMITER +CommonConstants.SAVA_IMG_PATH_CATEGORY
					+ FileUtil.FILE_DIRECTORY_DELIMITER + categoryId + FileUtil.FILE_DIRECTORY_DELIMITER + surveyResultId + FileUtil.FILE_DIRECTORY_DELIMITER;
					FileUtil.registTargetFile(surveyResultImgPath, content.getSurveyResult(), content.getSurveyResultImageFile());
				}
			}
		}
	}

	// FormデータをEntityへ変換
	private void convertCategoryFormToEntity(SurveyCategoryUpdateForm surveyCategoryUpdateForm, SurveyCategory category) throws Exception {
		category.setId(surveyCategoryUpdateForm.getId());
		category.setSurveyManagementId(surveyCategoryUpdateForm.getSurveyManagementId());
		category.setSurveyCategoryName(surveyCategoryUpdateForm.getSurveyCategoryName());
		category.setSurveyCategoryColor(surveyCategoryUpdateForm.getSurveyCategoryColor());
		category.setSurveySummaryDecidePoint(surveyCategoryUpdateForm.getSurveySummaryDecidePoint());
		category.setSurveySummaryTitleAbove(surveyCategoryUpdateForm.getSurveySummaryTitleAbove());
		category.setSurveySummaryDetailAbove(surveyCategoryUpdateForm.getSurveySummaryDetailAbove());
		category.setSurveySummaryTitleBelow(surveyCategoryUpdateForm.getSurveySummaryTitleBelow());
		category.setSurveySummaryDetailBelow(surveyCategoryUpdateForm.getSurveySummaryDetailBelow());
		category.setSurveySummaryInduceAbove(surveyCategoryUpdateForm.getSurveySummaryInduceAbove());
		category.setSurveySummaryInduceBelow(surveyCategoryUpdateForm.getSurveySummaryInduceBelow());
		// 総合評価画像(Above)
		if(surveyCategoryUpdateForm.getSurveySummaryImageAboveFile() != null && StringUtil.isNotEmpty(surveyCategoryUpdateForm.getSurveySummaryImageAboveFile().getOriginalFilename()) ) {
			String uploadFileName = surveyCategoryUpdateForm.getSurveySummaryImageAboveFile().getOriginalFilename();
			String externalKey = uploadFileName.substring(uploadFileName.lastIndexOf("."));
			category.setSurveySummaryImageAbove(FileUtil.forbiddenCharacterReplace(surveyCategoryUpdateForm.getSurveySummaryTitleAbove() + externalKey));
		}else {
			category.setSurveySummaryImageAbove(surveyCategoryUpdateForm.getSurveySummaryImageAbove());
		}
		// 総合評価画像(Below)
		if(surveyCategoryUpdateForm.getSurveySummaryImageBelowFile() != null && StringUtil.isNotEmpty(surveyCategoryUpdateForm.getSurveySummaryImageBelowFile().getOriginalFilename()) ) {
			String uploadFileName = surveyCategoryUpdateForm.getSurveySummaryImageBelowFile().getOriginalFilename();
			String externalKey = uploadFileName.substring(uploadFileName.lastIndexOf("."));
			category.setSurveySummaryImageBelow(FileUtil.forbiddenCharacterReplace(surveyCategoryUpdateForm.getSurveySummaryTitleBelow() + externalKey));
		}else {
			category.setSurveySummaryImageBelow(surveyCategoryUpdateForm.getSurveySummaryImageBelow());
		}
		
		// カテゴリーコンテンツ
		List<CategoryContentDto> categoryContentLst = new ArrayList<CategoryContentDto>();
		if(surveyCategoryUpdateForm.getCategoryContentLst() != null && surveyCategoryUpdateForm.getCategoryContentLst().size() != 0  ) {
			// 評価結果ID取得
			Integer resultContentId ;
			// 新規登録の場合
			if(surveyCategoryUpdateForm.getId() == null) {
				resultContentId = 1;
			}else { // 更新の場合
				SurveyCategory existedCategory = surveyCategoryService.getSurveyCategoryById(surveyCategoryUpdateForm.getId() );
				Type listType = new TypeToken<ArrayList<CategoryContentDto>>(){}.getType();
				List<CategoryContentDto> existedContentLst = (new Gson()).fromJson(existedCategory.getSurveyCategoryContent(), listType);
				
				Optional<CategoryContentDto> maxOptional = existedContentLst.stream()
																									.max(Comparator.comparing(CategoryContentDto::getId, Comparator.nullsFirst(Integer::compareTo)));
				if (maxOptional.isPresent()) {
					resultContentId = maxOptional.get().getId() != null ? maxOptional.get().getId() +1: 1 ;
				} else {
					resultContentId = 1;
				}
			}
			
			for(CategoryContentDto content : surveyCategoryUpdateForm.getCategoryContentLst()) {
				CategoryContentDto contentDto = new CategoryContentDto();
				// 評価コンテンツID設定
				if(content.getId() != null) {
					contentDto.setId(content.getId());
				}else {
					contentDto.setId(resultContentId);
					resultContentId++;
				}
				contentDto.setPointFrom(content.getPointFrom() != null ? content.getPointFrom() : 0);
				contentDto.setPointTo(content.getPointTo()!= null ? content.getPointTo() : 0);
				contentDto.setSurveyResult(content.getSurveyResult() != null ? content.getSurveyResult() : "");
				contentDto.setSurveyResultDetail(content.getSurveyResultDetail() != null ? content.getSurveyResultDetail() : "");
				if(content.getSurveyResultImageFile() != null && StringUtil.isNotEmpty(content.getSurveyResultImageFile().getOriginalFilename()) ) {
					String uploadFileName = content.getSurveyResultImageFile().getOriginalFilename();
					String externalKey = uploadFileName.substring(uploadFileName.lastIndexOf("."));
					contentDto.setSurveyResultImage(FileUtil.forbiddenCharacterReplace(content.getSurveyResult() + externalKey));
				}else {
					contentDto.setSurveyResultImage(content.getSurveyResultImage()!= null ? content.getSurveyResultImage() : "");
				}
				categoryContentLst.add(contentDto);
			}
		}
		// リストをJsonへ変換
		if(categoryContentLst.size() != 0 ) {
			category.setSurveyCategoryContent(new Gson().toJson(categoryContentLst));
		}
	}

	@GetMapping("/surveyContentDetail/surveyCategoryUpdate")
	public ModelAndView surveyCategoryUpdate(
			HttpServletRequest request,
			HttpSession session ,
			@RequestParam(value="categoryId", required = true, defaultValue="-1") Integer categoryId) throws Exception {
		ModelAndView mav = new ModelAndView();
		// セッションからユーザ情報取得
		User user = (User) session.getAttribute(CommonConstants.SESSION_KEY_USER_LOGIN);
		
		SurveyCategory category = surveyCategoryService.getSurveyCategoryById(categoryId);
		
		SurveyManagement survey = surveyContentService.getSurveyContentByIdAndUserId(category.getSurveyManagementId(), user.getId());
		mav.addObject("survey", survey);
		
		SurveyCategoryUpdateForm surveyCategoryUpdateForm = new SurveyCategoryUpdateForm();
		this.convertEntityToCategoryForm(category, surveyCategoryUpdateForm, survey.getSurveyPatternId());
		
		mav.addObject("surveyCategoryUpdateForm", surveyCategoryUpdateForm);
		// リファラ
		mav.addObject("referer", request.getHeader("referer"));
		mav.setViewName("surveyCategoryUpdate");
		
		return mav;
	}
	
	// Entityからフォームへ変換
	private void convertEntityToCategoryForm(SurveyCategory category, SurveyCategoryUpdateForm surveyCategoryUpdateForm, Integer patternId) {
		surveyCategoryUpdateForm.setId(category.getId());
		surveyCategoryUpdateForm.setSurveyManagementId(category.getSurveyManagementId());
		surveyCategoryUpdateForm.setSurveyCategoryName(category.getSurveyCategoryName());
		surveyCategoryUpdateForm.setSurveyCategoryColor(category.getSurveyCategoryColor());
		
		if(patternId !=CommonConstants.PARTTERN_FLOW && patternId !=CommonConstants.PARTTERN_COMPLEX_TOTAL) {
			surveyCategoryUpdateForm.setSurveySummaryDecidePoint(category.getSurveySummaryDecidePoint());
			surveyCategoryUpdateForm.setSurveySummaryTitleAbove(category.getSurveySummaryTitleAbove());
			surveyCategoryUpdateForm.setSurveySummaryDetailAbove(category.getSurveySummaryDetailAbove());
			surveyCategoryUpdateForm.setSurveySummaryImageAbove(category.getSurveySummaryImageAbove());
			surveyCategoryUpdateForm.setSurveySummaryInduceAbove(category.getSurveySummaryInduceAbove());
			surveyCategoryUpdateForm.setSurveySummaryTitleBelow(category.getSurveySummaryTitleBelow());
			surveyCategoryUpdateForm.setSurveySummaryDetailBelow(category.getSurveySummaryDetailBelow());
			surveyCategoryUpdateForm.setSurveySummaryImageBelow(category.getSurveySummaryImageBelow());
			surveyCategoryUpdateForm.setSurveySummaryInduceBelow(category.getSurveySummaryInduceBelow());
			// 総合評価画像(Above)
			try {
				String imgFileName = category.getSurveySummaryImageAbove();
				String imgFile = imgSavePath + FileUtil.FILE_DIRECTORY_DELIMITER + category.getSurveyManagementId() + FileUtil.FILE_DIRECTORY_DELIMITER +CommonConstants.SAVA_IMG_PATH_CATEGORY + FileUtil.FILE_DIRECTORY_DELIMITER 
										+ category.getId() +  FileUtil.FILE_DIRECTORY_DELIMITER + CommonConstants.SAVA_IMG_PATH_SUMMARY_ABOVE + FileUtil.FILE_DIRECTORY_DELIMITER 
										+ imgFileName;
				byte[] imgByte = Files.readAllBytes( new File(imgFile).toPath());
				String encodedImage = "data:image/" + imgFileName.substring(imgFileName.lastIndexOf(".") +1 ) + ";base64," 
						+ Base64.getEncoder().encodeToString(imgByte);
				surveyCategoryUpdateForm.setSurveySummaryImageAboveBase64(encodedImage);
			} catch (IOException e) {
				log.error("総合評価画像(判定点数以上)ファイル取得にエラーが発生しました。",e);
			}
			// 総合評価画像(Below)
			try {
				String imgFileName = category.getSurveySummaryImageBelow();
				String imgFile = imgSavePath + FileUtil.FILE_DIRECTORY_DELIMITER + category.getSurveyManagementId() + FileUtil.FILE_DIRECTORY_DELIMITER +CommonConstants.SAVA_IMG_PATH_CATEGORY + FileUtil.FILE_DIRECTORY_DELIMITER 
										+ category.getId() +  FileUtil.FILE_DIRECTORY_DELIMITER + CommonConstants.SAVA_IMG_PATH_SUMMARY_BELOW + FileUtil.FILE_DIRECTORY_DELIMITER 
										+ imgFileName;
				byte[] imgByte = Files.readAllBytes( new File(imgFile).toPath());
				String encodedImage = "data:image/" + imgFileName.substring(imgFileName.lastIndexOf(".") +1 ) + ";base64," 
						+ Base64.getEncoder().encodeToString(imgByte);
				surveyCategoryUpdateForm.setSurveySummaryImageBelowBase64(encodedImage);
			} catch (IOException e) {
				log.error("総合評価画像(判定点数以上)ファイル取得にエラーが発生しました。",e);
			}
		}
		
		// カテゴリーコンテンツ
		Type listType = new TypeToken<ArrayList<CategoryContentDto>>(){}.getType();
		List<CategoryContentDto> contentLst = (new Gson()).fromJson(category.getSurveyCategoryContent(), listType);  
		List<CategoryContentDto> categoryContentLst = new ArrayList<CategoryContentDto>();
		if(contentLst != null && contentLst.size() !=0 ) {
			for(CategoryContentDto content : contentLst ) {
				CategoryContentDto dto = new CategoryContentDto();
				dto.setId(content.getId());
				dto.setPointFrom(content.getPointFrom());
				dto.setPointTo(content.getPointTo());
				dto.setSurveyResult(content.getSurveyResult());
				dto.setSurveyResultDetail(content.getSurveyResultDetail());
				dto.setSurveyResultImage(content.getSurveyResultImage());
				
				// 評価結果画像
				try {
					if(patternId !=CommonConstants.PARTTERN_COMPLEX_POINT  && patternId != CommonConstants.PARTTERN_SINGULAR ) {
						String imgFileName = content.getSurveyResultImage();
						String imgFile = imgSavePath + FileUtil.FILE_DIRECTORY_DELIMITER + category.getSurveyManagementId() + FileUtil.FILE_DIRECTORY_DELIMITER +CommonConstants.SAVA_IMG_PATH_CATEGORY + FileUtil.FILE_DIRECTORY_DELIMITER 
												+ category.getId() +  FileUtil.FILE_DIRECTORY_DELIMITER + content.getId() + FileUtil.FILE_DIRECTORY_DELIMITER + imgFileName;
						byte[] imgByte = Files.readAllBytes( new File(imgFile).toPath());
						String encodedImage = "data:image/" + imgFileName.substring(imgFileName.lastIndexOf(".") +1 ) + ";base64," 
								+ Base64.getEncoder().encodeToString(imgByte);
						dto.setSurveyResultImgBase64(encodedImage);
					}
				} catch (IOException e) {
					log.error("診断軸の評価結果画像ファイル取得にエラーが発生しました。",e);
				}
				
				categoryContentLst.add(dto);
			}
		}
		surveyCategoryUpdateForm.setCategoryContentLst(categoryContentLst);
		
	}

	@PostMapping("/surveyContentDetail/surveyCategoryUpdate/exec")
	public ModelAndView surveyCategoryUpdateExec(
			HttpServletRequest request,
			HttpSession session ,
			SurveyCategoryUpdateForm surveyCategoryUpdateForm) throws Exception {
		ModelAndView mav = new ModelAndView();
		
		// セッションからユーザ情報取得して、ユーザIDと診断コンテンツIDで情報検証
		User user = (User) session.getAttribute(CommonConstants.SESSION_KEY_USER_LOGIN);
		surveyContentService.getSurveyContentByIdAndUserId(surveyCategoryUpdateForm.getSurveyManagementId(), user.getId());
		
		// カテゴリーデータ更新
		SurveyCategory category = new SurveyCategory();
		this.convertCategoryFormToEntity(surveyCategoryUpdateForm, category);
		surveyCategoryService.surveyCategoryUpdate(category);
		
		// カテゴリー関連画像更新
		this.surveyCategoryImageRegist(surveyCategoryUpdateForm.getId(), surveyCategoryUpdateForm, category);
		
		mav.setViewName("redirect:/surveyContentList/contentDetail?contentId=" + surveyCategoryUpdateForm.getSurveyManagementId() );
		
		return mav;
	}
	
	@GetMapping("/surveyContentDetail/surveyCategoryDelete")
	public ModelAndView surveyCategoryDelete(
			HttpServletRequest request,
			HttpSession session ,
			@RequestParam(value="categoryId", required = true, defaultValue="-1") Integer categoryId) throws Exception {
		ModelAndView mav = new ModelAndView();
		// セッションからユーザ情報取得し、ユーザ削除可能の対象か検証
		User user = (User) session.getAttribute(CommonConstants.SESSION_KEY_USER_LOGIN);
		SurveyCategory category = surveyCategoryService.getSurveyCategoryById(categoryId);
		surveyContentService.getSurveyContentByIdAndUserId(category.getSurveyManagementId(), user.getId());
		
		surveyCategoryService.surveyCategoryDelete(categoryId);
		
		mav.setViewName("redirect:/surveyContentList/contentDetail?contentId=" + category.getSurveyManagementId());
		
		return mav;
	}
}
