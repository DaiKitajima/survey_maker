package jp.co.SurveyMaker.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.co.SurveyMaker.Repository.SurveyQuestionLink.SurveyQuestionLinkRepository;
import jp.co.SurveyMaker.Service.Entity.SurveyQuestionLink;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor
public class SurveyQuestionLinkService {
	@Autowired
	private  SurveyQuestionLinkRepository surveyQuestionLinkRepository;
	
	// 質問リンク情報登録/更新
	public void surveyQuestionLinkUpdate(SurveyQuestionLink questionLink) throws Exception {
		surveyQuestionLinkRepository.save(questionLink);
	}
	
	// 質問リンク情報リスト更新
	public void surveyQuestionLinkLstUpdate(List<SurveyQuestionLink> questionLinkLst) throws Exception {
		if(questionLinkLst != null && questionLinkLst.size()!=0 ) {
			for(SurveyQuestionLink link: questionLinkLst) {
				try {
					this.surveyQuestionLinkUpdate(link);
				} catch (Exception e) {
					throw e;
				}
			}
		}
	}
	
	// 質問リンク情報取得
	public List<SurveyQuestionLink> getSurveyQuestionLinkLst(Integer contentId) throws Exception {
		return surveyQuestionLinkRepository.findBySurveyManagementIdAndDeleteFlgFalse(contentId);
	}
	
	// 指定質問のリンク情報取得
	public List<SurveyQuestionLink> getSurveyQuestionLinkLstByQuestionId(Integer contentId, Integer questionId) throws Exception {
		return surveyQuestionLinkRepository.findBySurveyManagementIdAndSurveyQuestionIdAndDeleteFlgFalse(contentId, questionId);
	}
	
	// 質問リンク情報削除
	public void deleteQuestionLinkByContentIdAndQuestionId(Integer contentId,Integer questionId) throws Exception {
		surveyQuestionLinkRepository.deleteBySurveyManagementIdAndSurveyQuestionId(contentId, questionId);
	}
	
}
