package jp.co.SurveyMaker.Form;

import org.springframework.web.multipart.MultipartFile;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter
@ToString
public class SurveyContentUpdateForm {
	
	private Integer id;
	
	private Integer userId;
	
	private String surveyName;
	
	private MultipartFile surveyImgFile;
	
	private String surveyImage;
	
	// base64の画像表示
	private String surveyImageBase64;
	
	private String surveyColor;
	
	private Integer surveyPatternId;
}
