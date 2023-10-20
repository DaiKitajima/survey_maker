package jp.co.SurveyMaker.Repository.SurveyPattern;

import org.springframework.stereotype.Repository;

import jp.co.SurveyMaker.Repository.Common.CommonRepository;


@Repository
public interface SurveyPatternRepository extends AutomaticallyImplementedSurveyPatternRepository,ManuallyImplementedSurveyPatternRepository, CommonRepository{
	
}
