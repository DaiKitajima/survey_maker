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
	
	private MultipartFile surveyHeaderImgFile;
	
	private String surveyHeaderImage;
	
	// base64の画像表示
	private String surveyHeaderImageBase64;
	
	private String surveyDescription;
	
	private String surveyInduceUrl;
	
	private String surveyColor;
	
	private Integer surveyPatternId;
}
