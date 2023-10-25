package jp.co.SurveyMaker.Repository.SurveyQuestion;

import org.springframework.stereotype.Repository;

import jp.co.SurveyMaker.Repository.Common.CommonRepository;


@Repository
public interface SurveyQuestionRepository extends AutomaticallyImplementedSurveyQuestionRepository,ManuallyImplementedSurveyQuestionRepository, CommonRepository{
	
}
