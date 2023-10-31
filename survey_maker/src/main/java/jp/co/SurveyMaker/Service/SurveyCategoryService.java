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
	
	// IDより、データを取得
	public SurveyCategory getSurveyCategoryById(Integer id) throws Exception {
		return surveyCategoryRepository.findByIdAndDeleteFlgFalse(id).orElseThrow();
	}
	
	// カテゴリー登録
	public Integer surveyCategoryRegist(SurveyCategory category) throws Exception {
		surveyCategoryRepository.save(category);
		return surveyCategoryRepository.getLastInsertId();
	}
	
	// カテゴリー更新
	public void surveyCategoryUpdate(SurveyCategory category) throws Exception {
		surveyCategoryRepository.save(category);
	}
	
	// カテゴリー削除
	public void surveyCategoryDelete(Integer id) throws Exception {
		surveyCategoryRepository.deleteById(id);
	}
}
