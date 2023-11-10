package jp.co.SurveyMaker.Form;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter
@ToString
public class CategoryResultForm {
	
	private Integer categoryId;
    
	private String categoryName;
	
	private Integer categoryPoint;
	
	private Integer categoryResultId;
	
    private String categoryResultTitle;
    
    private String categoryResultImgBase64;
    
    private String categoryResultDetail;

}
