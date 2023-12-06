package jp.co.SurveyMaker.Dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter
@ToString
public class QuestionPositionDto {
	// 質問ID または カテゴリーID
    private Integer id;
	// チャート図のy軸
    private Integer top;
   // チャート図のx軸
    private Integer left;
}
