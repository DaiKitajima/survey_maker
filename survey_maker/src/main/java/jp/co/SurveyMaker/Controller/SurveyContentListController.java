package jp.co.SurveyMaker.Controller;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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
import jp.co.SurveyMaker.Form.QuestionLinkForm;
import jp.co.SurveyMaker.Form.SurveyCategoryUpdateForm;
import jp.co.SurveyMaker.Form.SurveyContentDetailForm;
import jp.co.SurveyMaker.Form.SurveyContentListForm;
import jp.co.SurveyMaker.Form.SurveyContentUpdateForm;
import jp.co.SurveyMaker.Service.SurveyCategoryService;
import jp.co.SurveyMaker.Service.SurveyContentListService;
import jp.co.SurveyMaker.Service.SurveyContentService;
import jp.co.SurveyMaker.Service.SurveyPatternService;
import jp.co.SurveyMaker.Service.SurveyQuestionLinkService;
import jp.co.SurveyMaker.Service.SurveyQuestionService;
import jp.co.SurveyMaker.Service.Entity.SurveyCategory;
import jp.co.SurveyMaker.Service.Entity.SurveyManagement;
import jp.co.SurveyMaker.Service.Entity.SurveyPattern;
import jp.co.SurveyMaker.Service.Entity.SurveyQuestion;
import jp.co.SurveyMaker.Service.Entity.SurveyQuestionLink;
import jp.co.SurveyMaker.Service.Entity.User;
import jp.co.SurveyMaker.Util.FileUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class SurveyContentListController {
	 
	@Autowired
	private SurveyContentListService surveyContentListService;
	
	@Autowired
	private SurveyContentService surveyContentService;
	
	@Autowired
	private SurveyCategoryService surveyCategoryService;
	
	@Autowired
	private SurveyPatternService surveyPatternService;
	
	@Autowired
	private SurveyQuestionService surveyQuestionService;
	
	@Autowired
	private SurveyQuestionLinkService surveyQuestionLinkService;
	
	 @Value("${server.image.save.path}")
	 private String imgSavePath;
	 
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
		List<SurveyContentUpdateForm> surveyContentList = new ArrayList<SurveyContentUpdateForm>();
		this.convertEntityToForm(surveyContentList, surveyContentListService.surveyContentSearch(condition));
		surveyContentListForm.setSurveyContentList(surveyContentList) ;
		
		List<SurveyPattern> patterns = surveyPatternService.getAllPattern();
		mav.addObject("patternLst", patterns);
		mav.addObject("surveyContentListForm", surveyContentListForm);
		mav.setViewName("/surveyContentList");
		
		return mav;
	}
	
	private void convertEntityToForm(List<SurveyContentUpdateForm> surveyContentList,
			List<SurveyManagement> searchedList) {
		if(searchedList != null && searchedList.size() != 0 ) {
			for(SurveyManagement content : searchedList) {
				SurveyContentUpdateForm form = new SurveyContentUpdateForm();
				form.setId(content.getId());
				form.setUserId(content.getUserId());
				form.setSurveyColor(content.getSurveyColor());

				try {
					String imgFileName = content.getSurveyImage();
					String imgFile = imgSavePath + FileUtil.FILE_DIRECTORY_DELIMITER + content.getId() + FileUtil.FILE_DIRECTORY_DELIMITER + imgFileName;
					byte[] imgByte = Files.readAllBytes( new File(imgFile).toPath());
					String encodedImage = "data:image/" + imgFileName.substring(imgFileName.lastIndexOf(".") +1 ) + ";base64," 
							+ Base64.getEncoder().encodeToString(imgByte);
					form.setSurveyImageBase64(encodedImage);
				} catch (IOException e) {
					log.error("コンテンツ画像ファイル取得にエラーが発生しました。",e);
				}
				
				form.setSurveyImage(content.getSurveyImage());
				form.setSurveyName(content.getSurveyName());
				form.setSurveyPatternId(content.getSurveyPatternId());
				
				surveyContentList.add(form);
			}
		}
		
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
		
		List<SurveyContentUpdateForm> surveyContentList = new ArrayList<SurveyContentUpdateForm>();
		this.convertEntityToForm(surveyContentList, surveyContentListService.surveyContentSearch(condition));
		surveyContentListForm.setSurveyContentList(surveyContentList) ;
		
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
		// セッションからユーザ情報取得して、コンテンツ所属検証
		User user = (User) session.getAttribute(CommonConstants.SESSION_KEY_USER_LOGIN);
		SurveyManagement survey =  surveyContentService.getSurveyContentByIdAndUserId(contentId, user.getId());

		SurveyContentDetailForm surveyContentDetailForm = new SurveyContentDetailForm();
		surveyContentDetailForm.setContentId(contentId);
		
		// 診断コンテンツフォーム設定
		SurveyContentUpdateForm contentForm = new SurveyContentUpdateForm();
		this.convertEntityToSurveyContentForm(survey, contentForm);
		surveyContentDetailForm.setSurveyContent(contentForm);
		
		// 診断軸コンテンツフォーム設定
		List<SurveyCategory> categoryLst =  surveyCategoryService.getSurveyCategoryByContentId(contentId);
		List<SurveyCategoryUpdateForm> categoryFormLst = new ArrayList<SurveyCategoryUpdateForm>();
		this.convertEntityToCategoryLstForm(categoryLst, categoryFormLst, survey.getSurveyPatternId());
		surveyContentDetailForm.setCategoryLst(categoryFormLst);
		
		// 質問コンテンツフォーム設定
		List<SurveyQuestion> questionLst = surveyQuestionService.getSurveyQuestionByContentIdOrderByOrderNo(contentId);
		List<QuestionContentUpdateForm> questionFormLst = new ArrayList<QuestionContentUpdateForm>();
		this.convertEntityToQuestionLstForm(questionLst, questionFormLst);
		surveyContentDetailForm.setQuestionFormLst(questionFormLst);
		
		// 質問リンクフォーム設定
		List<SurveyQuestionLink> questionLinkLst = surveyQuestionLinkService.getSurveyQuestionLinkLst(contentId);
		List<QuestionLinkForm> questionLinkFormLst = new ArrayList<QuestionLinkForm>();
		this.convertEntityToQuestionLinkLstForm(questionLinkLst, questionLinkFormLst);
		surveyContentDetailForm.setQuestionLinkLst(questionLinkFormLst);
		
		List<SurveyPattern> patterns = surveyPatternService.getAllPattern();
		mav.addObject("patternLst", patterns);
		
		mav.addObject("linkTypeLst", Stream.of(LinkType.values()).collect(Collectors.toMap(t->t.getCode(),  t->t.getDisplay())).entrySet().stream().toList());
		
		mav.addObject(LinkType.NEXT_QUESTION.name(), LinkType.NEXT_QUESTION);
		mav.addObject(LinkType.SURVEY_RESULT.name(), LinkType.SURVEY_RESULT);
		
		mav.addObject("surveyContentDetailForm", surveyContentDetailForm );
		
		// リファラ
		mav.addObject("referer", request.getHeader("referer"));
		mav.setViewName("/surveyContentDetail");
		
		return mav;
	}
	
	// 質問リンクフォーム変換
	private void convertEntityToQuestionLinkLstForm(List<SurveyQuestionLink> questionLinkLst,
			List<QuestionLinkForm> questionLinkFormLst) {
		if(questionLinkLst != null && questionLinkLst.size() !=0) {
			questionLinkLst.forEach(link ->{
				QuestionLinkForm form = new QuestionLinkForm();
				this.convertEntityToQuestionLinkForm(link, form);
				questionLinkFormLst.add(form);
			});
		}
	}

	private void convertEntityToQuestionLinkForm(SurveyQuestionLink link, QuestionLinkForm linkForm) {
		linkForm.setId(link.getId());
		linkForm.setSurveyManagementId(link.getSurveyManagementId());
		linkForm.setAnswerId(link.getAnswerId());
		linkForm.setQuestionId(link.getSurveyQuestionId());
		linkForm.setLinkType(link.getLinkType());
		linkForm.setLinkTo(link.getLinkTo());
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
	
	// 診断軸コンテンツフォーム変換
	private void convertEntityToCategoryLstForm(List<SurveyCategory> categoryLst,
			List<SurveyCategoryUpdateForm> categoryFormLst, Integer patternId) {
		if( categoryLst!= null && categoryLst.size() != 0 ) {
			 categoryLst.forEach(category ->{
				 SurveyCategoryUpdateForm form = new SurveyCategoryUpdateForm();
				 this.convertEntityToCategoryForm(category, form, patternId);
				 categoryFormLst.add(form);
			 });
		}
	}

	// 診断軸Entityからフォームへ変換
	private void convertEntityToCategoryForm(SurveyCategory category, SurveyCategoryUpdateForm surveyCategoryUpdateForm, Integer patternId) {
		surveyCategoryUpdateForm.setId(category.getId());
		surveyCategoryUpdateForm.setSurveyManagementId(category.getSurveyManagementId());
		surveyCategoryUpdateForm.setSurveyCategoryName(category.getSurveyCategoryName());
		
		// 総合評価部分
		if(patternId !=CommonConstants.PARTTERN_FLOW && patternId !=CommonConstants.PARTTERN_COMPLEX_TOTAL) {
			surveyCategoryUpdateForm.setSurveySummaryDecidePoint(category.getSurveySummaryDecidePoint());
			surveyCategoryUpdateForm.setSurveySummaryTitleAbove(category.getSurveySummaryTitleAbove());
			surveyCategoryUpdateForm.setSurveySummaryDetailAbove(category.getSurveySummaryDetailAbove());
			surveyCategoryUpdateForm.setSurveySummaryImageAbove(category.getSurveySummaryImageAbove());
			surveyCategoryUpdateForm.setSurveySummaryTitleBelow(category.getSurveySummaryTitleBelow());
			surveyCategoryUpdateForm.setSurveySummaryDetailBelow(category.getSurveySummaryDetailBelow());
			surveyCategoryUpdateForm.setSurveySummaryImageBelow(category.getSurveySummaryImageBelow());
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
					String imgFileName = content.getSurveyResultImage();
					String imgFile = imgSavePath + FileUtil.FILE_DIRECTORY_DELIMITER + category.getSurveyManagementId() + FileUtil.FILE_DIRECTORY_DELIMITER +CommonConstants.SAVA_IMG_PATH_CATEGORY + FileUtil.FILE_DIRECTORY_DELIMITER 
											+ category.getId() +  FileUtil.FILE_DIRECTORY_DELIMITER + content.getId() + FileUtil.FILE_DIRECTORY_DELIMITER + imgFileName;
					byte[] imgByte = Files.readAllBytes( new File(imgFile).toPath());
					String encodedImage = "data:image/" + imgFileName.substring(imgFileName.lastIndexOf(".") +1 ) + ";base64," 
							+ Base64.getEncoder().encodeToString(imgByte);
					dto.setSurveyResultImgBase64(encodedImage);
				} catch (IOException e) {
					log.error("総合評価画像(判定点数以上)ファイル取得にエラーが発生しました。",e);
				}
				
				categoryContentLst.add(dto);
			}
		}
		surveyCategoryUpdateForm.setCategoryContentLst(categoryContentLst);
	}
	
	// 診断コンテンツフォーム変換
	private void convertEntityToSurveyContentForm(SurveyManagement surveyContent,
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
		surveyContentUpdateForm.setSurveyImage(surveyContent.getSurveyImage());
		surveyContentUpdateForm.setSurveyName(surveyContent.getSurveyName());
		surveyContentUpdateForm.setSurveyPatternId(surveyContent.getSurveyPatternId());
	}

	
	@GetMapping("/surveyContentList/contentDelete")
	public ModelAndView surveyContentDelete(
			HttpServletRequest request,
			HttpSession session,
			@RequestParam(value="contentId", required = true, defaultValue="-1") Integer contentId ) throws Exception {
		ModelAndView mav = new ModelAndView();
		// セッションからユーザ情報取得して、コンテンツ所属検証
		User user = (User) session.getAttribute(CommonConstants.SESSION_KEY_USER_LOGIN);
		surveyContentService.getSurveyContentByIdAndUserId(contentId, user.getId());
		
		// 診断コンテンツ一括削除
		surveyContentListService.delAllSurveyContent(contentId);
		
		mav.setViewName("redirect:/surveyContentList");
		
		return mav;
	}
}
