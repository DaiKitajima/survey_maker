package jp.co.SurveyMaker.Form;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

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
    
    private String surveySummaryDecidePoint;
    
    private String surveySummaryTitleAbove;
    
    private String surveySummaryImageAbove;
    
    private MultipartFile surveySummaryImageAboveFile;
    
    private String surveySummaryDetailAbove;
    
    private String surveySummaryTitleBelow;
    
    private String surveySummaryImageBelow;
    
    private MultipartFile surveySummaryImageBelowFile;
    
    private String surveySummaryDetailBelow;
    
    private String surveyCategoryContent;
    
    private List<CategoryContentDto> categoryContentLst;
    
}
