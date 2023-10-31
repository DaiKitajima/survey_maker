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
		
		mav.setViewName("/surveyContentRegist");
		
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
		
		Integer contentId = surveyContentService.surveyContentRegist(surveyContent);
		String savePath = imgSavePath + FileUtil.FILE_DIRECTORY_DELIMITER + contentId;
		FileUtil.registTargetFile(savePath, surveyContentUpdateForm.getSurveyName(), surveyContentUpdateForm.getSurveyImgFile());
		
		mav.setViewName("redirect:/surveyContentList");
		
		return mav;
	}
	
	private void convertSurveyContentFormToEnt(SurveyContentUpdateForm surveyContentUpdateForm,
			SurveyManagement surveyContent) {
		surveyContent.setId(surveyContentUpdateForm.getId());
		surveyContent.setUserId(surveyContentUpdateForm.getUserId());
		surveyContent.setSurveyColor(surveyContentUpdateForm.getSurveyColor());
		// アップロードファイル名取得
		if(surveyContentUpdateForm.getSurveyImgFile() != null && StringUtil.isNotEmpty(surveyContentUpdateForm.getSurveyImgFile().getOriginalFilename()) ) {
			String uploadFileName = surveyContentUpdateForm.getSurveyImgFile().getOriginalFilename();
			String externalKey = uploadFileName.substring(uploadFileName.lastIndexOf("."));
			surveyContent.setSurveyImage(surveyContentUpdateForm.getSurveyName() + externalKey);
		}else {
			surveyContent.setSurveyImage(surveyContentUpdateForm.getSurveyImage());
		}
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
		mav.setViewName("/surveyContentUpdate");
		
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
		
		surveyContentUpdateForm.setSurveyImage(surveyContent.getSurveyImage());
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
		
		mav.setViewName("redirect:/surveyContentList/contentDetail?id=" + surveyContentUpdateForm.getId() );
		
		return mav;
	}
}
