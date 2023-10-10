package jp.co.SurveyMaker.Repository.User;

import org.springframework.stereotype.Repository;

import jp.co.SurveyMaker.Repository.Common.CommonRepository;


@Repository
public interface UserRepository extends AutomaticallyImplementedUserRepository,ManuallyImplementedUserRepository, CommonRepository{
	
}
