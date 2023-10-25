package jp.co.SurveyMaker.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.co.SurveyMaker.Repository.SurveyCategory.SurveyCategoryRepository;
import jp.co.SurveyMaker.Repository.SurveyManagement.SurveyManagementRepository;
import jp.co.SurveyMaker.Repository.SurveyQuestion.SurveyQuestionRepository;
import jp.co.SurveyMaker.Repository.SurveyQuestionLink.SurveyQuestionLinkRepository;
import jp.co.SurveyMaker.Repository.SurveyResult.SurveyResultRepository;
import jp.co.SurveyMaker.Service.Entity.SurveyManagement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor
public class SurveyContentListService {
	@Autowired
	private  SurveyManagementRepository surveyManagementRepository;
	
	@Autowired
	private  SurveyResultRepository surveyResultRepository;
	
	@Autowired
	private SurveyQuestionLinkRepository surveyQuestionLinkRepository;
	
	@Autowired
	private SurveyQuestionRepository surveyQuestionRepository;
	
	@Autowired
	private SurveyCategoryRepository surveyCategoryRepository;
	
	// 診断コンテンツ検索
	public List<SurveyManagement> surveyContentSearch(SurveyManagement condition) throws Exception {
		return surveyManagementRepository.surveyContentSearch(condition);
	}

	// 診断コンテンツ一括削除
	public void delAllSurveyContent(Integer contentId) {
		// 診断結果削除
		surveyResultRepository.deleteBySurveyManagementId(contentId);
		// 質問リンク削除
		surveyQuestionLinkRepository.deleteBySurveyManagementId(contentId);
		// 質問削除
		surveyQuestionRepository.deleteBySurveyManagementId(contentId);
		// 診断軸削除
		surveyCategoryRepository.deleteBySurveyManagementId(contentId);
		// 診断コンテンツ削除
		surveyManagementRepository.deleteById(contentId);
	}
}
