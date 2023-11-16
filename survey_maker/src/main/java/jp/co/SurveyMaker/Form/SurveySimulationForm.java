package jp.co.SurveyMaker.Form;

import java.util.List;

import jp.co.SurveyMaker.Dto.AnswerResultDto;
import jp.co.SurveyMaker.Service.Entity.SurveyManagement;
import jp.co.SurveyMaker.Service.Entity.SurveyQuestionLink;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter
@ToString
public class SurveySimulationForm {
	
	private SurveyManagement surveyContent;

	// 診断質問リスト
    private List<QuestionContentUpdateForm> questionFormLst;
	
	// 回答結果Map(質問ID,回答ID)
	private List<AnswerResultDto> answerResultLst;
	
	// 回答リンクリスト
	private List<SurveyQuestionLink> questionLinkLst;

}
