package jp.co.SurveyMaker.Controller;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jp.co.SurveyMaker.Constants.CommonConstants;
import jp.co.SurveyMaker.Constants.LinkType;
import jp.co.SurveyMaker.Dto.AnswerContentDto;
import jp.co.SurveyMaker.Dto.QuestionOrderDto;
import jp.co.SurveyMaker.Dto.QuestionPositionDto;
import jp.co.SurveyMaker.Form.QuestionAndLinkForm;
import jp.co.SurveyMaker.Service.SurveyCategoryService;
import jp.co.SurveyMaker.Service.SurveyContentService;
import jp.co.SurveyMaker.Service.SurveyQuestionLinkService;
import jp.co.SurveyMaker.Service.SurveyQuestionService;
import jp.co.SurveyMaker.Service.Entity.SurveyCategory;
import jp.co.SurveyMaker.Service.Entity.SurveyManagement;
import jp.co.SurveyMaker.Service.Entity.SurveyQuestion;
import jp.co.SurveyMaker.Service.Entity.SurveyQuestionLink;
import jp.co.SurveyMaker.Service.Entity.User;
import jp.co.SurveyMaker.Util.FileUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/internalApi")
public class RestSurveyMakerController {
	
	@Value("${server.image.save.path}")
	 private String imgSavePath;
	
	@Autowired
	private SurveyQuestionService surveyQuestionService;
	
	@Autowired
	private SurveyQuestionLinkService surveyQuestionLinkService;
	
	@Autowired
	private SurveyContentService surveyContentService;
	
	@Autowired
	private SurveyCategoryService surveyCategoryService;
	
	@GetMapping("/getSurveyQuestionContent")
	public String getSurveyQuestionContent(
			HttpServletRequest request,
			HttpSession session,
			@RequestParam(value="contentId", required = true) Integer contentId,
			@RequestParam(value="linkTo", required = true) Integer linkTo) throws Exception {
		// セッションからユーザ情報取得
		User user = (User) session.getAttribute(CommonConstants.SESSION_KEY_USER_LOGIN);
		SurveyManagement survey = surveyContentService.getSurveyContentByIdAndUserId(contentId, user.getId());
		
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
	
	@GetMapping("/questionOrLinkDelete")
	public void questionOrLinkDelete(
			HttpServletRequest request,
			HttpSession session ,
			@RequestParam(value="contentId", required = true) Integer contentId,
			@RequestParam(value="questionId", required = false) Integer questionId,
			@RequestParam(value="linkId", required = false) Integer linkId) throws Exception {
		// セッションからユーザ情報取得
		User user = (User) session.getAttribute(CommonConstants.SESSION_KEY_USER_LOGIN);
		SurveyManagement survey = surveyContentService.getSurveyContentByIdAndUserId(contentId, user.getId());

		// 質問削除
		if( questionId != null ) {
			try {
				SurveyQuestion question = surveyQuestionService.getSurveyQuestionById(questionId);
				// ユーザ所属のコンテンツか検証
				surveyContentService.getSurveyContentByIdAndUserId(question.getSurveyManagementId(), user.getId());
				// 質問コンテンツ削除
				surveyQuestionService.surveyQuestionAndLinkDelete(question);
			} catch (Exception e) {
				log.error("質問コンテンツ削除にエラーが発生しました。 ", e );
			}
			
			// 質問削除後、Order更新
			List<SurveyQuestion> questionLst =  surveyQuestionService.getSurveyQuestionByContentIdOrderByOrderNo(contentId);
			List<QuestionOrderDto> questionOrderLst = new ArrayList<QuestionOrderDto>();
			if( questionLst != null && questionLst.size() != 0) {
				for(int order =1;order<=questionLst.size();order++) {
					QuestionOrderDto dto = new QuestionOrderDto();
					dto.setQuestionId(questionLst.get(order - 1).getId());
					dto.setOrderNo(order);
					questionOrderLst.add(dto);
				}
			}
			surveyQuestionService.questionOrderUpdate(questionOrderLst);
		}else if(linkId != null){
			// 質問リンク削除
			surveyQuestionLinkService.deleteQuestionLinkByContentIdAndLinkId(contentId, linkId);
		}
	}
	
	@PostMapping("/questionLinkCreate")
	public Integer questionLinkCreate(
			HttpServletRequest request,
			HttpSession session ,
			@RequestBody Map<String,String> linkData) throws Exception {
		
	    Integer contentId = Integer.parseInt(linkData.get("contentId"));
	    String fromConnector = linkData.get("fromConnector");
	    String fromOperator = linkData.get("fromOperator");
	    String fromSubConnector = linkData.get("fromSubConnector");
	    String toConnector = linkData.get("toConnector");
	    String toOperator = linkData.get("toOperator");
	    String toSubConnector = linkData.get("toSubConnector");
	    
		try {
			// セッションからユーザ情報取得
			User user = (User) session.getAttribute(CommonConstants.SESSION_KEY_USER_LOGIN);
			SurveyManagement survey = surveyContentService.getSurveyContentByIdAndUserId(contentId, user.getId());
			
			SurveyQuestionLink link = new SurveyQuestionLink();
			link.setSurveyManagementId(contentId);
			link.setSurveyQuestionId( Integer.parseInt( fromOperator.replace("operator", "")) );
			link.setAnswerId( Integer.parseInt( fromConnector.replace("output_", "")) );
			if(toOperator.equals(CommonConstants.FLOW_CHART_RESULT)) {
				link.setLinkType(LinkType.SURVEY_RESULT.getCode());
			}else {
				link.setLinkType(LinkType.NEXT_QUESTION.getCode());
			}
			link.setLinkTo( Integer.parseInt( toConnector.replace("input_", "")) );
			if(!surveyQuestionLinkService.existsBySurveyManagementIdAndSurveyQuestionIdAndAnswerId(link)) {
				return surveyQuestionLinkService.surveyQuestionLinkRegist(link);
			}
		} catch (Exception e) {
			log.error("フローチャートにリンク設定時、エラーが発生しました。 ", e );
		}
		return null;
	}
	
	@PostMapping("/questionPositionSave/{contentId}")
	public boolean questionPositionSave(
			HttpServletRequest request,
			HttpSession session ,
			@PathVariable("contentId") Integer contentId,
			@RequestBody String jsonData) throws Exception {
		try {
			// セッションからユーザ情報取得
			User user = (User) session.getAttribute(CommonConstants.SESSION_KEY_USER_LOGIN);
			SurveyManagement survey = surveyContentService.getSurveyContentByIdAndUserId(contentId, user.getId());
			// 質問位置
			List<QuestionPositionDto> positionLst = new ArrayList<QuestionPositionDto>();
			// 評価結果位置
			QuestionPositionDto resultPosition = new QuestionPositionDto();
			
			Gson gson = new Gson();
			JsonElement element  = gson.fromJson(jsonData, JsonElement.class);
			Map<String, JsonElement> map = element.getAsJsonObject().asMap();
			if(map != null ) {
				for(String key: map.keySet()) {
					JsonElement value = map.get(key);
					JsonObject  positionVal = value.getAsJsonObject();
					
					if(!CommonConstants.FLOW_CHART_RESULT.equals(key) ) {
						QuestionPositionDto dto = new QuestionPositionDto();
						dto.setId(Integer.valueOf(key.replace("operator", "")) );
						dto.setLeft( positionVal.get("left").getAsInt());
						dto.setTop(positionVal.get("top").getAsInt());
						positionLst.add(dto);
					}else {
						resultPosition.setLeft(positionVal.get("left").getAsInt());
						resultPosition.setTop(positionVal.get("top").getAsInt());
					}
				}
			}
			
			// 質問位置更新
			if(positionLst.size() != 0 ) {
				surveyQuestionService.questionPositionUpdate(positionLst);
			}
			// 評価結果位置更新
			if( resultPosition != null ) {
				// フローの場合、１カテゴリーのみあり
				SurveyCategory category = surveyCategoryService.getSurveyCategoryByContentId(contentId).get(0);
				resultPosition.setId(category.getId());
				category.setSurveyCategoryPosition((new Gson()).toJson(resultPosition));
				surveyCategoryService.surveyCategoryUpdate(category);
			}
		} catch (Exception e) {
			log.error("フローチャートに位置保存時、エラーが発生しました。 ", e );
			return false;
		}
		return true;
	}
}
