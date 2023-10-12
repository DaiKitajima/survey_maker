package jp.co.SurveyMaker.Form;

import java.util.List;

import jp.co.SurveyMaker.Dto.CategoryContentDto;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter
@ToString
public class SurveyCategoryUpdateForm {
	
    private Integer id;

    private Integer surveyManagementId;
    
    private String surveyCategoryName;
    
    private String surveyCategoryContent;
    
    private List<CategoryContentDto> categoryContentLst;
    
}
