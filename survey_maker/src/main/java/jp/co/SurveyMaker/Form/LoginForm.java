package jp.co.SurveyMaker.Form;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter
@ToString
public class LoginForm {

	private String password;

	private String loginId;
	// TODO 現状未使用
	private String companyCode;
}
