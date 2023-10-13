package jp.co.SurveyMaker.Dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter
@ToString
public class AnswerContentDto {
	
    private Integer answerId;
    
    private String answer;
    
    private List<AnswerPointDto> answerPointLst;
}
