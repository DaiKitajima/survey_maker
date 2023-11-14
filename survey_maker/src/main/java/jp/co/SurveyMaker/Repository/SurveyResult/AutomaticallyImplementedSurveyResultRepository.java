package jp.co.SurveyMaker.Repository.SurveyResult;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.Repository;

import jp.co.SurveyMaker.Service.Entity.SurveyResult;


@NoRepositoryBean
public interface AutomaticallyImplementedSurveyResultRepository extends Repository<SurveyResult, Integer> {
	//存在判定
	boolean existsByIdAndDeleteFlgFalse(Integer id);
	//１件取得(id識別)
	Optional<SurveyResult> findByIdAndDeleteFlgFalse(Integer id);
	//１件取得(key識別)
	Optional<SurveyResult> findBySurveyKeyAndDeleteFlgFalse(String key);
	//全取得
	List<SurveyResult> findAll();
	//全取得(未削除)
	List<SurveyResult> findByDeleteFlgFalse();
	//登録・更新
	<S extends SurveyResult> S save(S entity);
	//削除
	void deleteById(Integer id);
	
	//診断コンテンツIDより削除
	void deleteBySurveyManagementId(Integer contentId);
}
