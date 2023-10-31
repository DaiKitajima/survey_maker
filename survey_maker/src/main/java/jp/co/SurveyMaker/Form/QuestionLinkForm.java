package jp.co.SurveyMaker.Form;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter
@ToString
public class QuestionLinkForm {
	
	private Integer id;
	
    private Integer questionId;
    
    private Integer answerId;
    
    private Integer linkType;
    
    private Integer linkTo;
}
