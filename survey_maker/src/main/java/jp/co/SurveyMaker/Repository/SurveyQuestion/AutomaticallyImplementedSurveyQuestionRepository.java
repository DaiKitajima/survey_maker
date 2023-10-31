package jp.co.SurveyMaker.Repository.SurveyQuestion;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.Repository;

import jp.co.SurveyMaker.Service.Entity.SurveyQuestion;


@NoRepositoryBean
public interface AutomaticallyImplementedSurveyQuestionRepository extends Repository<SurveyQuestion, Integer> {
	//存在判定
	boolean existsByIdAndDeleteFlgFalse(Integer id);
	//１件取得(id識別)
	Optional<SurveyQuestion> findByIdAndDeleteFlgFalse(Integer id);
	//全取得
	List<SurveyQuestion> findAll();
	//全取得(未削除)
	List<SurveyQuestion> findByDeleteFlgFalse();
	//診断コンテンツIDより、当該コンテンツの質問全取得(未削除)
	List<SurveyQuestion> findBySurveyManagementIdAndDeleteFlgFalse(Integer contentId);
	//診断コンテンツIDより、当該コンテンツの質問全取得(昇順ソート)
	List<SurveyQuestion> findBySurveyManagementIdAndDeleteFlgFalseOrderByQuestionOrderNoAsc(Integer contentId);
	//登録・更新
	<S extends SurveyQuestion> S save(S entity);
	//削除
	void deleteById(Integer id);
	
	//診断コンテンツIDより削除
	void deleteBySurveyManagementId(Integer contentId);
}
