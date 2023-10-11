package jp.co.SurveyMaker.Form;

import java.util.List;

import jp.co.SurveyMaker.Service.Entity.SurveyCategory;
import jp.co.SurveyMaker.Service.Entity.SurveyQuestion;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter
@ToString
public class SurveyContentDetailForm {
	
	private Integer id;
	
	private Integer userId;
	
	private String surveyName;
	
	private String surveyImage;
	
	private Integer surveyPatternId;
	
	// 診断結果カテゴリーリスト
	private List<SurveyCategory> surveyCategoryLst;

	// 診断質問リスト
	private List<SurveyQuestion> surveyQuestionLst;
	
}
