package jp.co.SurveyMaker.Repository.SurveyResult;

import org.springframework.stereotype.Repository;

import jp.co.SurveyMaker.Repository.Common.CommonRepository;


@Repository
public interface SurveyResultRepository extends AutomaticallyImplementedSurveyResultRepository,ManuallyImplementedSurveyResultRepository, CommonRepository{
	
}
