package jp.co.SurveyMaker.Repository.SurveyPattern;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.Repository;

import jp.co.SurveyMaker.Service.Entity.SurveyPattern;


@NoRepositoryBean
public interface AutomaticallyImplementedSurveyPatternRepository extends Repository<SurveyPattern, Integer> {
	//存在判定
	boolean existsByIdAndDeleteFlgFalse(Integer id);
	//１件取得(id識別)
	Optional<SurveyPattern> findByIdAndDeleteFlgFalse(Integer id);
	//全取得
	List<SurveyPattern> findAll();
	//全取得(未削除)
	List<SurveyPattern> findByDeleteFlgFalse();
	//登録・更新
	<S extends SurveyPattern> S save(S entity);
	//削除
	void deleteById(Integer id);
}
