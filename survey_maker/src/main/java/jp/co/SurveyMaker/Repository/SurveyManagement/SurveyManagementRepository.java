package jp.co.SurveyMaker.Repository.SurveyManagement;

import org.springframework.stereotype.Repository;

import jp.co.SurveyMaker.Repository.Common.CommonRepository;


@Repository
public interface SurveyManagementRepository extends AutomaticallyImplementedSurveyManagementRepository,ManuallyImplementedSurveyManagementRepository, CommonRepository{
	
}
