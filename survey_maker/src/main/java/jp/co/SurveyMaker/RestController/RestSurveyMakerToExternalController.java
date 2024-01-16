package jp.co.SurveyMaker.RestController;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jp.co.SurveyMaker.Constants.CommonConstants;
import jp.co.SurveyMaker.Dto.AnswerContentDto;
import jp.co.SurveyMaker.Form.QuestionAndLinkForm;
import jp.co.SurveyMaker.Service.SurveyQuestionLinkService;
import jp.co.SurveyMaker.Service.SurveyQuestionService;
import jp.co.SurveyMaker.Service.Entity.SurveyQuestion;
import jp.co.SurveyMaker.Util.FileUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api")
public class RestSurveyMakerToExternalController {
	
	@Value("${server.image.save.path}")
	 private String imgSavePath;
	
	@Autowired
	private SurveyQuestionService surveyQuestionService;
	
	@Autowired
	private SurveyQuestionLinkService surveyQuestionLinkService;
	
	@GetMapping("/getSurveyQuestionContent")
	public String getSurveyQuestionContent(
			HttpServletRequest request,
			HttpSession session,
			@RequestParam(value="contentId", required = true) Integer contentId,
			@RequestParam(value="linkTo", required = true) Integer linkTo) throws Exception {
		
		QuestionAndLinkForm resultForm = new QuestionAndLinkForm();
		SurveyQuestion question = surveyQuestionService.getSurveyQuestionById(linkTo);
		resultForm.setQuestionId(question.getId());
		resultForm.setQuestionImage(question.getQuestionImage());
		resultForm.setQuestionOrderNo(question.getQuestionOrderNo());
		resultForm.setQuestionTitle(question.getQuestionTitle());
		resultForm.setSurveyManagementId(question.getSurveyManagementId());
		
		// 回答
		Type listType = new TypeToken<ArrayList<AnswerContentDto>>(){}.getType();
		List<AnswerContentDto> answerContentLst = (new Gson()).fromJson(question.getAnswerContent(), listType);
		resultForm.setAnswerContentLst(answerContentLst);
		
		// 質問画像
		try {
			String imgFileName = question.getQuestionImage();
			String imgFile = imgSavePath + FileUtil.FILE_DIRECTORY_DELIMITER + question.getSurveyManagementId() + FileUtil.FILE_DIRECTORY_DELIMITER +CommonConstants.SAVA_IMG_PATH_QUESTION + FileUtil.FILE_DIRECTORY_DELIMITER 
									+ question.getId() +  FileUtil.FILE_DIRECTORY_DELIMITER +  imgFileName;
			byte[] imgByte = Files.readAllBytes( new File(imgFile).toPath());
			String encodedImage = "data:image/" + imgFileName.substring(imgFileName.lastIndexOf(".") +1 ) + ";base64," 
					+ Base64.getEncoder().encodeToString(imgByte);
			resultForm.setQuestionImageBase64(encodedImage);
		} catch (IOException e) {
			log.error("質問画像ファイル取得にエラーが発生しました。",e);
		}
		// リンク設定
		resultForm.setQuestionLinkLst(surveyQuestionLinkService.getSurveyQuestionLinkLstByQuestionId(contentId, question.getId()));
		
		return (new Gson()).toJson(resultForm) ;
	}
}
