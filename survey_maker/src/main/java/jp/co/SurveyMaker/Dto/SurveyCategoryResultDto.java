package jp.co.SurveyMaker.Dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter
@ToString
public class SurveyCategoryResultDto {
	
    private Integer categoryId;
    
    private String categoryName;
    
    private Integer categoryTotalPoint;
    
    private Integer categoryResultId;
}
