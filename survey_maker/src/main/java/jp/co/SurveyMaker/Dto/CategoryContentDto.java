package jp.co.SurveyMaker.Dto;

import org.springframework.web.multipart.MultipartFile;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter
@ToString
public class CategoryContentDto {
	
	private Integer id;
	
    private Integer pointFrom;

    private Integer pointTo;
    
    private String surveyResult;
    
    private String surveyResultDetail;
    
    private String surveyResultImage;
    
    private MultipartFile surveyResultImageFile;
}
