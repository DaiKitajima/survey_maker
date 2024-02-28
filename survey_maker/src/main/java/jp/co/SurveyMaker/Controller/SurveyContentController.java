package jp.co.SurveyMaker.Controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import jp.co.SurveyMaker.Util.FileUtil;
import jp.co.SurveyMaker.Util.StringUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class SurveyContentController {
	
	@Autowired
	private SurveyContentService surveyContentService;
	
	@Autowired
	private SurveyPatternService surveyPatternService;
	 
	 @Value("${server.image.save.path}")
	 private String imgSavePath;
	 
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
		
		// リファラ
		mav.addObject("referer", request.getHeader("referer"));
		mav.setViewName("surveyContentRegist");
		
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
		
		// コンテンツ画像登録
		Integer contentId = surveyContentService.surveyContentRegist(surveyContent);
		String savePath = imgSavePath + FileUtil.FILE_DIRECTORY_DELIMITER + contentId;
		FileUtil.registTargetFile(savePath, surveyContentUpdateForm.getSurveyName(), surveyContentUpdateForm.getSurveyImgFile());
		
		// コンテンツSP画像登録
		FileUtil.registTargetFile(savePath, surveyContentUpdateForm.getSurveyName() + CommonConstants.SAVA_IMG_SP_NAME, surveyContentUpdateForm.getSurveyImgSpFile());
		
		// コンテンツヘッダ画像登録
		FileUtil.registTargetFile(savePath, surveyContentUpdateForm.getSurveyName() + CommonConstants.SAVA_IMG_HEADER_NAME, surveyContentUpdateForm.getSurveyHeaderImgFile());
		
		mav.setViewName("redirect:/surveyContentList");
		
		return mav;
	}
	
	private void convertSurveyContentFormToEnt(SurveyContentUpdateForm surveyContentUpdateForm,
			SurveyManagement surveyContent) throws Exception{
		surveyContent.setId(surveyContentUpdateForm.getId());
		surveyContent.setUserId(surveyContentUpdateForm.getUserId());
		surveyContent.setSurveyColor(surveyContentUpdateForm.getSurveyColor());
		// アップロードファイル名取得(コンテンツ画像)
		if(surveyContentUpdateForm.getSurveyImgFile() != null && StringUtil.isNotEmpty(surveyContentUpdateForm.getSurveyImgFile().getOriginalFilename()) ) {
			String uploadFileName = surveyContentUpdateForm.getSurveyImgFile().getOriginalFilename();
			String externalKey = uploadFileName.substring(uploadFileName.lastIndexOf("."));
			surveyContent.setSurveyImage(FileUtil.forbiddenCharacterReplace(surveyContentUpdateForm.getSurveyName() + externalKey));
		}else {
			surveyContent.setSurveyImage(surveyContentUpdateForm.getSurveyImage());
		}
		
		// アップロードファイル名取得(コンテンツ画像SP)
		if(surveyContentUpdateForm.getSurveyImgSpFile() != null && StringUtil.isNotEmpty(surveyContentUpdateForm.getSurveyImgSpFile().getOriginalFilename()) ) {
			String uploadFileName = surveyContentUpdateForm.getSurveyImgSpFile().getOriginalFilename();
			String externalKey = uploadFileName.substring(uploadFileName.lastIndexOf("."));
			surveyContent.setSurveyImageSp(FileUtil.forbiddenCharacterReplace(surveyContentUpdateForm.getSurveyName() + CommonConstants.SAVA_IMG_SP_NAME + externalKey));
		}else {
			surveyContent.setSurveyImageSp(surveyContentUpdateForm.getSurveyImageSp());
		}
		
		// アップロードファイル名取得(コンテンツヘッダ画像)
		if(surveyContentUpdateForm.getSurveyHeaderImgFile() != null && StringUtil.isNotEmpty(surveyContentUpdateForm.getSurveyHeaderImgFile().getOriginalFilename()) ) {
			String uploadFileName = surveyContentUpdateForm.getSurveyHeaderImgFile().getOriginalFilename();
			String externalKey = uploadFileName.substring(uploadFileName.lastIndexOf("."));
			surveyContent.setSurveyHeaderImage(FileUtil.forbiddenCharacterReplace(surveyContentUpdateForm.getSurveyName() + CommonConstants.SAVA_IMG_HEADER_NAME + externalKey));
		}else {
			surveyContent.setSurveyHeaderImage(surveyContentUpdateForm.getSurveyHeaderImage());
		}
		surveyContent.setSurveyInduceArea(surveyContentUpdateForm.getSurveyInduceArea());
		surveyContent.setSurveyDescription(surveyContentUpdateForm.getSurveyDescription());
		surveyContent.setSummaryDisplayFlg(surveyContentUpdateForm.getSummaryDisplayFlg());
		surveyContent.setSurveyName(surveyContentUpdateForm.getSurveyName());
		surveyContent.setSurveyPatternId(surveyContentUpdateForm.getSurveyPatternId());
	}

	@GetMapping("/surveyContentDetail/surveyContentUpdate")
	public ModelAndView surveyContentUpdate(
			HttpServletRequest request,
			HttpSession session ,
			@RequestParam(value="contentId", required = true, defaultValue="-1") Integer contentId) throws Exception {
		ModelAndView mav = new ModelAndView();
		// セッションからユーザ情報取得
		User user = (User) session.getAttribute(CommonConstants.SESSION_KEY_USER_LOGIN);
		
		SurveyManagement surveyContent = surveyContentService.getSurveyContentByIdAndUserId(contentId, user.getId());
		SurveyContentUpdateForm surveyContentUpdateForm = new SurveyContentUpdateForm();
		this.convertSurveyContentEntityToForm(surveyContent, surveyContentUpdateForm);
		mav.addObject("surveyContentUpdateForm", surveyContentUpdateForm);
		// リファラ
		mav.addObject("referer", request.getHeader("referer"));
		mav.setViewName("surveyContentUpdate");
		
		return mav;
	}
	
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
			String imgFileName = surveyContent.getSurveyImageSp();
			String imgFile = imgSavePath + FileUtil.FILE_DIRECTORY_DELIMITER + surveyContent.getId() + FileUtil.FILE_DIRECTORY_DELIMITER + imgFileName;
			byte[] imgByte = Files.readAllBytes( new File(imgFile).toPath());
			String encodedImage = "data:image/" + imgFileName.substring(imgFileName.lastIndexOf(".") +1 ) + ";base64," 
					+ Base64.getEncoder().encodeToString(imgByte);
			surveyContentUpdateForm.setSurveyImageSpBase64(encodedImage);
		} catch (IOException e) {
			log.error("コンテンツSP画像ファイル取得にエラーが発生しました。",e);
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
		surveyContentUpdateForm.setSurveyImageSp(surveyContent.getSurveyImageSp());
		surveyContentUpdateForm.setSurveyDescription(surveyContent.getSurveyDescription());
		surveyContentUpdateForm.setSurveyInduceArea(surveyContent.getSurveyInduceArea());
		surveyContentUpdateForm.setSummaryDisplayFlg(surveyContent.getSummaryDisplayFlg());
		surveyContentUpdateForm.setSurveyName(surveyContent.getSurveyName());
		surveyContentUpdateForm.setSurveyPatternId(surveyContent.getSurveyPatternId());
	}

	@PostMapping("/surveyContentDetail/surveyContentUpdate/exec")
	public ModelAndView surveyContentUpdateExec(
			HttpServletRequest request,
			HttpSession session ,
			SurveyContentUpdateForm surveyContentUpdateForm) throws Exception {
		ModelAndView mav = new ModelAndView();
		
		SurveyManagement surveyContent = new SurveyManagement ();
		this.convertSurveyContentFormToEnt(surveyContentUpdateForm, surveyContent);
		
		surveyContentService.surveyContentUpdate(surveyContent);
		if(surveyContentUpdateForm.getSurveyImgFile() != null ) {
			String savePath = imgSavePath + FileUtil.FILE_DIRECTORY_DELIMITER + surveyContentUpdateForm.getId();
			FileUtil.registTargetFile(savePath, surveyContentUpdateForm.getSurveyName(), surveyContentUpdateForm.getSurveyImgFile());
		}
		
		if(surveyContentUpdateForm.getSurveyImgSpFile() != null ) {
			String savePath = imgSavePath + FileUtil.FILE_DIRECTORY_DELIMITER + surveyContentUpdateForm.getId();
			FileUtil.registTargetFile(savePath, surveyContentUpdateForm.getSurveyName() +CommonConstants.SAVA_IMG_SP_NAME , surveyContentUpdateForm.getSurveyImgSpFile());
		}
		
		if(surveyContentUpdateForm.getSurveyHeaderImgFile() != null ) {
			String savePath = imgSavePath + FileUtil.FILE_DIRECTORY_DELIMITER + surveyContentUpdateForm.getId();
			FileUtil.registTargetFile(savePath, surveyContentUpdateForm.getSurveyName() +CommonConstants.SAVA_IMG_HEADER_NAME , surveyContentUpdateForm.getSurveyHeaderImgFile());
		}
		
		mav.setViewName("redirect:/surveyContentList/contentDetail?contentId=" + surveyContentUpdateForm.getId() );
		
		return mav;
	}
}
