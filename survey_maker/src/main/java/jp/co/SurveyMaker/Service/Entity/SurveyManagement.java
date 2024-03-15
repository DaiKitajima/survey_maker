package jp.co.SurveyMaker.Service.Entity;

import org.hibernate.annotations.SQLDelete;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@Getter @Setter
@SQLDelete(sql="update survey_management set delete_flg=1 where id= ?")
@Table(name = "survey_management")
public class SurveyManagement {
	
	   @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Integer id;

	    @Column(name = "user_id")
	    private Integer userId;
	    
	    @Column(name = "survey_name")
	    private String surveyName;
	    
	    @Column(name = "survey_image")
	    private String surveyImage;
	    
	    @Column(name = "survey_image_sp")
	    private String surveyImageSp;
	    
	    @Column(name = "survey_color")
	    private String surveyColor;
	    
	    @Column(name = "survey_result_color")
	    private String surveyResultColor;
	    
	    @Column(name = "survey_pattern_id")
	    private Integer surveyPatternId;
	    
	    @Column(name = "survey_header_image")
	    private String surveyHeaderImage;
	    
	    @Column(name = "survey_description")
	    private String surveyDescription;
	    
	    @Column(name = "survey_induce_area")
	    private String surveyInduceArea;
	    
	    @Column(name = "summary_display_flg")
	    private Integer summaryDisplayFlg;
	    
	    @Column(name = "delete_flg")
	    private boolean deleteFlg;
	
}
