package jp.co.SurveyMaker.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.co.SurveyMaker.Repository.SurveyCategory.SurveyCategoryRepository;
import jp.co.SurveyMaker.Service.Entity.SurveyCategory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor
public class SurveyCategoryService {
	@Autowired
	private  SurveyCategoryRepository surveyCategoryRepository;
	
	// コンテンツIDより、各軸を取得
	public List<SurveyCategory> getSurveyCategoryByContentId(Integer contentId) throws Exception {
		return surveyCategoryRepository.findBySurveyManagementIdAndDeleteFlgFalse(contentId);
	}
}
