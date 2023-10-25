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
@SQLDelete(sql="update survey_summary_result set delete_flg=1 where id= ?")
@Table(name = "survey_summary_result")
public class SurveySummaryResult {
	
	   @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Integer id;

	    @Column(name = "survey_management_id")
	    private Integer surveyManagementId;
	    
	    @Column(name = "survey_summary_title")
	    private String surveySummaryTitle;
	    
	    @Column(name = "survey_summary_text")
	    private String surveySummaryText;
	    
	    @Column(name = "survey_summary_image")
	    private String surveySummaryImage;
	    
	    @Column(name = "survey_summary_from")
	    private String  surveySummaryFrom;
	    
	    @Column(name = "survey_summary_to")
	    private String  surveySummaryTo;
	    
	    @Column(name = "delete_flg")
	    private boolean deleteFlg;
	
}
