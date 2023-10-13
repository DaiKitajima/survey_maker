package jp.co.SurveyMaker.Dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter
@ToString
public class QuestionLinkDto {
	
	private Integer id;
	
    private Integer questionId;
    
    private Integer answerId;
    
    private Integer linkType;
    
    private Integer linkTo;
}
