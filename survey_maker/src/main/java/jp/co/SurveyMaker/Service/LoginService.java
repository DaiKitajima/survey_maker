package jp.co.SurveyMaker.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.co.SurveyMaker.Repository.User.UserRepository;
import jp.co.SurveyMaker.Service.Entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor
public class LoginService {
	@Autowired
	private  UserRepository userRepository;
	
	// テーブル情報登録
	public User login(User user) {
		return userRepository.findByLoginIdAndPasswordAndDeleteFlgFalse(user.getLoginId(), user.getPassword()).orElseThrow();
	}
}
