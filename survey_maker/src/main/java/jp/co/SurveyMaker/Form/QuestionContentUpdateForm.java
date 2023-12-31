package jp.co.SurveyMaker.Form;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import jp.co.SurveyMaker.Dto.AnswerContentDto;
import jp.co.SurveyMaker.Dto.QuestionPositionDto;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter
@ToString
public class QuestionContentUpdateForm {
	
    private Integer id;

    private Integer surveyManagementId;
    
    private Integer questionOrderNo;
    
    private String questionTitle;
    
    private String questionImage;
    
    private String questionImageBase64;
    
    private QuestionPositionDto position;
    
    private MultipartFile questionImgFile;
    
    private List<AnswerContentDto> answerContentLst;
    
}
