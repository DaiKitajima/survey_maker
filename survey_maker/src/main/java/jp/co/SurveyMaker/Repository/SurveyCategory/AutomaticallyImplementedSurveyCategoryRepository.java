package jp.co.SurveyMaker.Repository.SurveyCategory;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.Repository;

import jp.co.SurveyMaker.Service.Entity.SurveyCategory;


@NoRepositoryBean
public interface AutomaticallyImplementedSurveyCategoryRepository extends Repository<SurveyCategory, Integer> {
	//存在判定
	boolean existsByIdAndDeleteFlgFalse(Integer id);
	//１件取得(id識別)
	Optional<SurveyCategory> findByIdAndDeleteFlgFalse(Integer id);
	//全取得
	List<SurveyCategory> findAll();
	//全取得(未削除)
	List<SurveyCategory> findByDeleteFlgFalse();
	//診断コンテンツIDより、当該コンテンツのカテゴリー全取得(未削除)
	List<SurveyCategory> findBySurveyManagementIdAndDeleteFlgFalse(Integer contentId);
	//登録・更新
	<S extends SurveyCategory> S save(S entity);
	//削除
	void deleteById(Integer id);
	
	//診断コンテンツIDより削除
	void deleteBySurveyManagementId(Integer contentId);
}
