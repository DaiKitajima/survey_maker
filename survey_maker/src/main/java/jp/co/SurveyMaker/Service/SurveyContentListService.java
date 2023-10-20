package jp.co.SurveyMaker.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.co.SurveyMaker.Repository.SurveyManagement.SurveyManagementRepository;
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
	
	// 診断コンテンツ検索
	public List<SurveyManagement> surveyContentSearch(SurveyManagement condition) throws Exception {
		return surveyManagementRepository.surveyContentSearch(condition);
	}
}
