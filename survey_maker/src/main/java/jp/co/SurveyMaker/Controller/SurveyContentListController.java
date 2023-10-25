package jp.co.SurveyMaker.Controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jp.co.SurveyMaker.Constants.CommonConstants;
import jp.co.SurveyMaker.Form.SurveyContentDetailForm;
import jp.co.SurveyMaker.Form.SurveyContentListForm;
import jp.co.SurveyMaker.Form.SurveyContentUpdateForm;
import jp.co.SurveyMaker.Service.SurveyContentListService;
import jp.co.SurveyMaker.Service.SurveyPatternService;
import jp.co.SurveyMaker.Service.Entity.SurveyManagement;
import jp.co.SurveyMaker.Service.Entity.SurveyPattern;
import jp.co.SurveyMaker.Service.Entity.User;
import jp.co.SurveyMaker.Util.FileUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class SurveyContentListController {
	 
	@Autowired
	private SurveyContentListService surveyContentListService;
	
	@Autowired
	private SurveyPatternService surveyPatternService;
	
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
		
		mav.addObject("surveyContentDetailForm", new SurveyContentDetailForm());
		mav.setViewName("/surveyContentDetail");
		
		return mav;
	}
	
	@GetMapping("/surveyContentList/contentDelete")
	public ModelAndView surveyContentDelete(
			HttpServletRequest request,
			HttpSession session,
			@RequestParam(value="contentId", required = true, defaultValue="-1") Integer contentId ) throws Exception {
		ModelAndView mav = new ModelAndView();
		
		// 診断コンテンツ一括削除
		surveyContentListService.delAllSurveyContent(contentId);
		
		mav.setViewName("redirect:/surveyContentList");
		
		return mav;
	}
}
