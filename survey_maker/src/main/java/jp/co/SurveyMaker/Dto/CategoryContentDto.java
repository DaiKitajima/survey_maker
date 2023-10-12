package jp.co.SurveyMaker.Dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter
@ToString
public class CategoryContentDto {
	
    private Integer pointFrom;

    private Integer pointTo;
    
    private String surveyResult;
    
    private String surveyResultDetail;
    
    private String surveyResultImage;
}
