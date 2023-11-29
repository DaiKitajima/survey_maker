package jp.co.SurveyMaker.Repository.SurveyQuestionLink;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.Repository;

import jp.co.SurveyMaker.Service.Entity.SurveyQuestionLink;


@NoRepositoryBean
public interface AutomaticallyImplementedSurveyQuestionLinkRepository extends Repository<SurveyQuestionLink, Integer> {
	//存在判定
	boolean existsByIdAndDeleteFlgFalse(Integer id);
	//１件取得(id識別)
	Optional<SurveyQuestionLink> findByIdAndDeleteFlgFalse(Integer id);
	//全取得
	List<SurveyQuestionLink> findAll();
	//全取得(未削除)
	List<SurveyQuestionLink> findByDeleteFlgFalse();
	//診断コンテンツ所属のリンク情報取得
	List<SurveyQuestionLink> findBySurveyManagementIdAndDeleteFlgFalse(Integer contentId);
	//診断コンテンツ配下、指定質問のリンク情報取得
	List<SurveyQuestionLink> findBySurveyManagementIdAndSurveyQuestionIdAndDeleteFlgFalse(Integer contentId,Integer questionId);
	//登録・更新
	<S extends SurveyQuestionLink> S save(S entity);
	//削除
	void deleteById(Integer id);
	
	//診断コンテンツIDより削除
	void deleteBySurveyManagementId(Integer contentId);
	//診断コンテンツIDより削除
	void deleteBySurveyManagementIdAndId(Integer contentId,Integer id);
	//診断コンテンツID、質問IDより削除
	void deleteBySurveyManagementIdAndSurveyQuestionId(Integer contentId,Integer questionId);
}
