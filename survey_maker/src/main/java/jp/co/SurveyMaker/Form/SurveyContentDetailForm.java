package jp.co.SurveyMaker.Form;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter
@ToString
public class SurveyContentDetailForm {
	
	private Integer contentId;
	
	private SurveyContentUpdateForm surveyContent;
	
	// 診断軸リスト
	private List<SurveyCategoryUpdateForm> categoryLst;
	// 診断質問リスト
    private List<QuestionContentUpdateForm> questionFormLst;
    // 診断質問リンクリスト
    private List<QuestionLinkForm> questionLinkLst;
    
}
