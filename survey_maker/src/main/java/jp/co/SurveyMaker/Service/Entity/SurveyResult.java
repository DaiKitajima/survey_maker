package jp.co.SurveyMaker.Service.Entity;

import java.time.LocalTime;

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
@SQLDelete(sql="update survey_result set delete_flg=1 where id= ?")
@Table(name = "survey_result")
public class SurveyResult {
	
	   @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Integer id;

	    @Column(name = "survey_management_id")
	    private Integer surveyManagementId;
	    
	    @Column(name = "key")
	    private String key;
	    
	    @Column(name = "survey_summary_result_id")
	    private Integer surveySummaryResultId;
	    
	    @Column(name = "result_content")
	    private String resultContent;
	    
	    @Column(name = "expire_date")
	    private LocalTime  expireDate;
	    
	    @Column(name = "delete_flg")
	    private boolean deleteFlg;
	
}
