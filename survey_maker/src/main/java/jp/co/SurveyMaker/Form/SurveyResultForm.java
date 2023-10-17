package jp.co.SurveyMaker.Form;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter
@ToString
public class SurveyResultForm {
	
	private Integer id;
	
	private Integer surveyManagementId;
	
	private String key;
	
	private String resultContent;
	
	private SurveySummaryResultUpdateForm summary;
}
