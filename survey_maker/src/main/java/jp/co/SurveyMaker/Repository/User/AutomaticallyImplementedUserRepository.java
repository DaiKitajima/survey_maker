package jp.co.SurveyMaker.Repository.User;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.Repository;

import jp.co.SurveyMaker.Service.Entity.User;


@NoRepositoryBean
public interface AutomaticallyImplementedUserRepository extends Repository<User, Integer> {
	//存在判定
	boolean existsByIdAndDeleteFlgFalse(Integer id);
	//１件取得(id識別)
	Optional<User> findByIdAndDeleteFlgFalse(Integer id);
	//１件取得(ログインIDとパスワード識別)
	Optional<User> findByLoginIdAndPasswordAndDeleteFlgFalse(String loginId, String password);
	//全取得
	List<User> findAll();
	//全取得(未削除)
	List<User> findByDeleteFlgFalse();
	//登録・更新
	<S extends User> S save(S entity);
	//削除
	void deleteById(Integer id);
}
