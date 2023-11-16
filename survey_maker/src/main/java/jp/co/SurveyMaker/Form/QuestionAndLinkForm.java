package jp.co.SurveyMaker.Form;

import java.util.List;

import jp.co.SurveyMaker.Dto.AnswerContentDto;
import jp.co.SurveyMaker.Service.Entity.SurveyQuestionLink;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter
@ToString
public class QuestionAndLinkForm {
	
    private Integer questionId;

    private Integer surveyManagementId;
    
    private Integer questionOrderNo;
    
    private String questionTitle;
    
    private String questionImage;
    
    private String questionImageBase64;
    
    private List<AnswerContentDto> answerContentLst;
    
    private List<SurveyQuestionLink> questionLinkLst;
    
}
