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
	
	// コンテンツIDより、各軸を取得
	public List<SurveyQuestion> getSurveyQuestionByContentId(Integer contentId) throws Exception {
		return surveyQuestionRepository.findBySurveyManagementIdAndDeleteFlgFalse(contentId);
	}
}
