package jp.co.SurveyMaker.Repository.SurveyManagement;

import java.util.List;

import jp.co.SurveyMaker.Service.Entity.SurveyManagement;

public interface ManuallyImplementedSurveyManagementRepository {
	/**
	 * データ検索
	 * @return
	 * @throws Exception 
	 */
	public List<SurveyManagement> surveyContentSearch(SurveyManagement condition) throws Exception ;
}
