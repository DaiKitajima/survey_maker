package jp.co.SurveyMaker.Form;

import org.springframework.web.multipart.MultipartFile;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter
@ToString
public class SurveySummaryResultUpdateForm {
	
	private Integer id;
	
	private Integer surveyManagementId;
	
	private String surveySummaryTitle;
	
	private String surveySummaryText;
	
	private String surveySummaryImage;
	
	private MultipartFile surveySummaryImgFile;
	
	private String surveySummaryFrom;

	private String surveySummaryTo;
	
}
