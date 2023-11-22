package jp.co.SurveyMaker.Form;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter
@ToString
public class QuestionLinkUpdateForm {

    private Integer surveyManagementId;
    
    private List<QuestionContentUpdateForm> questionFormLst;
    
    private List<QuestionLinkForm> questionLinkLst;
    
    private String flowChartData;
}
