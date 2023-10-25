package jp.co.SurveyMaker.Service;

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
public class SurveyContentService {
	@Autowired
	private  SurveyManagementRepository surveyManagementRepository;
	 
	// 診断コンテンツ登録
	public Integer surveyContentRegist(SurveyManagement surveyContent) throws Exception {
		surveyManagementRepository.save(surveyContent);
		return  surveyManagementRepository.getLastInsertId();
	}
	
	// 診断コンテンツ更新
	public void surveyContentUpdate(SurveyManagement surveyContent) throws Exception {
		surveyManagementRepository.save(surveyContent);
	}
	
	// IDより、診断コンテンツ取得
	public SurveyManagement getSurveyContentById(Integer id, Integer userId) throws Exception {
		return surveyManagementRepository.findByIdAndUserIdAndDeleteFlgFalse(id, userId).orElseThrow();
	}
}
