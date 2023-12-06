package jp.co.SurveyMaker.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.Gson;

import jp.co.SurveyMaker.Constants.CommonConstants;
import jp.co.SurveyMaker.Constants.LinkType;
import jp.co.SurveyMaker.Dto.QuestionOrderDto;
import jp.co.SurveyMaker.Dto.QuestionPositionDto;
import jp.co.SurveyMaker.Repository.SurveyQuestion.SurveyQuestionRepository;
import jp.co.SurveyMaker.Repository.SurveyQuestionLink.SurveyQuestionLinkRepository;
import jp.co.SurveyMaker.Service.Entity.SurveyQuestion;
import jp.co.SurveyMaker.Util.FileUtil;
import jp.co.SurveyMaker.Util.StringUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor
public class SurveyQuestionService {
	@Autowired
	private  SurveyQuestionRepository surveyQuestionRepository;
	
	@Autowired
	private  SurveyQuestionLinkRepository surveyQuestionLinkRepository;
	
	 @Value("${server.image.save.path}")
	 private String imgSavePath;
	 
	// コンテンツIDより、各質問を取得
	public List<SurveyQuestion> getSurveyQuestionByContentId(Integer contentId) throws Exception {
		return surveyQuestionRepository.findBySurveyManagementIdAndDeleteFlgFalse(contentId);
	}
	// コンテンツIDより、各質問を取得（昇順ソート）
	public List<SurveyQuestion> getSurveyQuestionByContentIdOrderByOrderNo(Integer contentId) throws Exception {
		return surveyQuestionRepository.findBySurveyManagementIdAndDeleteFlgFalseOrderByQuestionOrderNoAsc(contentId);
	}
	
	// IDより、質問データを取得
	public SurveyQuestion getSurveyQuestionById(Integer id) throws Exception {
		return surveyQuestionRepository.findByIdAndDeleteFlgFalse(id).orElseThrow();
	}
	
	// 質問情報登録
	public Integer surveyQuestionRegist(SurveyQuestion question) throws Exception {
		surveyQuestionRepository.save(question);
		return surveyQuestionRepository.getLastInsertId();
	}
	
	// 質問情報更新
	public void surveyQuestionUpdate(SurveyQuestion question) throws Exception {
		surveyQuestionRepository.save(question);
	}
	
	// 質問情報削除
	public void surveyQuestionDelete(Integer questionId) throws Exception {
		surveyQuestionRepository.deleteById(questionId);
	}
	
	// 質問とリンク情報削除
	public void surveyQuestionAndLinkDelete(SurveyQuestion question) throws Exception {
		surveyQuestionRepository.deleteById(question.getId());
		// 削除質問からのリンクを削除
		surveyQuestionLinkRepository.deleteBySurveyManagementIdAndSurveyQuestionId(question.getSurveyManagementId(), question.getId());
		// 削除質問へのリンクを削除
		surveyQuestionLinkRepository.deleteBySurveyManagementIdAndLinkTypeAndLinkTo(question.getSurveyManagementId(), LinkType.NEXT_QUESTION.getCode(), question.getId());
	}
	
	// 質問順番更新
	public void questionOrderUpdate(List<QuestionOrderDto> questionOrderLst) throws Exception {
		if(questionOrderLst != null && questionOrderLst.size() != 0) {
			for(QuestionOrderDto question : questionOrderLst) {
				try {
					SurveyQuestion newQue = this.getSurveyQuestionById(question.getQuestionId());
					newQue.setQuestionOrderNo(question.getOrderNo());
					String oldQuestionImg = "";
					if( StringUtil.isNotEmpty(newQue.getQuestionImage()) ){
						oldQuestionImg = newQue.getQuestionImage();
						String externalKey = newQue.getQuestionImage().substring(newQue.getQuestionImage().lastIndexOf("."));
						newQue.setQuestionImage(CommonConstants.SAVA_IMG_NAME_QUESTION + newQue.getQuestionOrderNo() + externalKey );
					}
					this.surveyQuestionUpdate(newQue);
					
					String savePath = imgSavePath + FileUtil.FILE_DIRECTORY_DELIMITER + newQue.getSurveyManagementId() + FileUtil.FILE_DIRECTORY_DELIMITER +CommonConstants.SAVA_IMG_PATH_QUESTION
							+ FileUtil.FILE_DIRECTORY_DELIMITER + newQue.getId() + FileUtil.FILE_DIRECTORY_DELIMITER;
					FileUtil.renameTargetFile(savePath, oldQuestionImg, newQue.getQuestionImage());
					
				} catch (Exception e) {
					throw e;
				}
			}
		}
	}
	
	
	// フローチャート図上の質問位置更新
	public void questionPositionUpdate(List<QuestionPositionDto> positionLst)throws Exception {
		if(positionLst != null && positionLst.size() != 0) {
			for(QuestionPositionDto position : positionLst) {
				try {
					SurveyQuestion newQue = this.getSurveyQuestionById(position.getId());
					newQue.setQuestionPosition((new Gson()).toJson(position));
					this.surveyQuestionUpdate(newQue);
				} catch (Exception e) {
					throw e;
				}
			}
		}
	}
}
