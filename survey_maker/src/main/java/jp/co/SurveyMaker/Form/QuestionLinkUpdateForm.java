package jp.co.SurveyMaker.Form;

import java.util.List;

import jp.co.SurveyMaker.Dto.QuestionLinkDto;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter
@ToString
public class QuestionLinkUpdateForm {
	
    private Integer id;

    private Integer surveyManagementId;
    
    private List<QuestionContentUpdateForm> questionLst;
    
    private List<QuestionLinkDto> questionLinkLst;
    
}
