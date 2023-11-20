package jp.co.SurveyMaker.Form;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter
@ToString
public class CategoryPointForm {
	
    private Integer id;

    private Integer surveyManagementId;
    
    private String categoryName;
    
    private Integer categoryTotalPoint;
    
    private Integer categoryUnUsedPoint;
}
