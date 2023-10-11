package jp.co.SurveyMaker.Form;

import java.util.List;

import jp.co.SurveyMaker.Service.Entity.SurveyManagement;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter
@ToString
public class SurveyContentListForm {
	
	private Integer userId;
	
	private String surveyNameForSearch;
	
	private Integer surveyPatternIdForSearch;
	
	private List<SurveyManagement> surveyContentList;
}
