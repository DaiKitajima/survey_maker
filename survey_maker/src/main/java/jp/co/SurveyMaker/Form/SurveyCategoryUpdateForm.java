package jp.co.SurveyMaker.Form;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import jp.co.SurveyMaker.Dto.CategoryContentDto;
import jp.co.SurveyMaker.Dto.QuestionPositionDto;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter
@ToString
public class SurveyCategoryUpdateForm {
	
    private Integer id;

    private Integer surveyManagementId;
    
    private String surveyCategoryName;
    
    private String surveyCategoryColor;
    
    private Integer surveySummaryDecidePoint;
    
    private String surveySummaryTitleAbove;
    
    private String surveySummaryImageAbove;
    
    private String surveySummaryImageAboveBase64;
    
    private MultipartFile surveySummaryImageAboveFile;
    
    private String surveySummaryDetailAbove;
    
    private String surveySummaryInduceAbove;
    
    private String surveySummaryTitleBelow;
    
    private String surveySummaryImageBelow;
    
    private String surveySummaryImageBelowBase64;
    
    private MultipartFile surveySummaryImageBelowFile;
    
    private String surveySummaryDetailBelow;
    
    private String surveySummaryInduceBelow;
    
    private String surveyCategoryContent;
    
    private QuestionPositionDto surveyCategoryPosition;
    
    private List<CategoryContentDto> categoryContentLst;
    
}
