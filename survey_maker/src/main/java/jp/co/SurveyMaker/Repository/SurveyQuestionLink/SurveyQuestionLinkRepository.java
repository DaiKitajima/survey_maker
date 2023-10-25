package jp.co.SurveyMaker.Repository.SurveyQuestionLink;

import org.springframework.stereotype.Repository;

import jp.co.SurveyMaker.Repository.Common.CommonRepository;


@Repository
public interface SurveyQuestionLinkRepository extends AutomaticallyImplementedSurveyQuestionLinkRepository,ManuallyImplementedSurveyQuestionLinkRepository, CommonRepository{
	
}
