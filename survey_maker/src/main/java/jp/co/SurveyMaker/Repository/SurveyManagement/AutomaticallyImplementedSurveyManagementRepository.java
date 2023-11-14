package jp.co.SurveyMaker.Repository.SurveyManagement;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.Repository;

import jp.co.SurveyMaker.Service.Entity.SurveyManagement;


@NoRepositoryBean
public interface AutomaticallyImplementedSurveyManagementRepository extends Repository<SurveyManagement, Integer> {
	//存在判定
	boolean existsByIdAndDeleteFlgFalse(Integer id);
	//１件取得(id識別)
	Optional<SurveyManagement> findByIdAndUserIdAndDeleteFlgFalse(Integer id,Integer userId);
	//１件取得(id識別)
	Optional<SurveyManagement> findByIdAndDeleteFlgFalse(Integer id);
	//全取得
	List<SurveyManagement> findAll();
	//全取得(未削除)
	List<SurveyManagement> findByDeleteFlgFalse();
	//登録・更新
	<S extends SurveyManagement> S save(S entity);
	//削除
	void deleteById(Integer id);
}
