package jp.co.SurveyMaker.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.co.SurveyMaker.Repository.SurveyQuestion.SurveyQuestionRepository;
import jp.co.SurveyMaker.Service.Entity.SurveyQuestion;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor
public class SurveyQuestionService {
	@Autowired
	private  SurveyQuestionRepository surveyQuestionRepository;
	
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
}
