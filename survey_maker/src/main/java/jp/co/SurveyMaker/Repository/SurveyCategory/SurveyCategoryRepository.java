package jp.co.SurveyMaker.Repository.SurveyCategory;

import org.springframework.stereotype.Repository;

import jp.co.SurveyMaker.Repository.Common.CommonRepository;


@Repository
public interface SurveyCategoryRepository extends AutomaticallyImplementedSurveyCategoryRepository,ManuallyImplementedSurveyCategoryRepository, CommonRepository{
	
}
