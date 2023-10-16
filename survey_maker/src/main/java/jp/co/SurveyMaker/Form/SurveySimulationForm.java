package jp.co.SurveyMaker.Form;

import java.util.List;
import java.util.Map;

import jp.co.SurveyMaker.Dto.AnswerContentDto;
import jp.co.SurveyMaker.Service.Entity.SurveyQuestion;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter
@ToString
public class SurveySimulationForm {
	
	private Integer id;
	
	private Integer userId;
	
	// 診断コンテンツ名
	private String surveyName;
	
	// 診断画像
	private String surveyImage;
	
	// 診断パターン
	private Integer surveyPatternId;

	// 診断質問リスト
	private List<SurveyQuestion> surveyQuestionLst;
	
	// 回答リスト
	private Map<Integer, List<AnswerContentDto>> answerMap;
	
	public Map<Integer, List<AnswerContentDto>> getAnswerMap(){
		return answerMap;
	}
}
