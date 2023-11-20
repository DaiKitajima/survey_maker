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
@SQLDelete(sql="update survey_category set delete_flg=1 where id= ?")
@Table(name = "survey_category")
public class SurveyCategory {
	
	   @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Integer id;

	    @Column(name = "survey_management_id")
	    private Integer surveyManagementId;
	    
	    @Column(name = "survey_category_name")
	    private String surveyCategoryName;
	    
	    @Column(name = "survey_category_color")
	    private String surveyCategoryColor;
	    
	    @Column(name = "survey_category_content")
	    private String surveyCategoryContent;
	    
	    @Column(name = "survey_summary_decide_point")
	    private Integer surveySummaryDecidePoint;
	    
	    @Column(name = "survey_summary_title_above")
	    private String surveySummaryTitleAbove;
	    
	    @Column(name = "survey_summary_image_above")
	    private String surveySummaryImageAbove;
	    
	    @Column(name = "survey_summary_detail_above")
	    private String surveySummaryDetailAbove;
	    
	    @Column(name = "survey_summary_title_below")
	    private String surveySummaryTitleBelow;
	    
	    @Column(name = "survey_summary_image_below")
	    private String surveySummaryImageBelow;
	    
	    @Column(name = "survey_summary_detail_below")
	    private String surveySummaryDetailBelow;
	    
	    @Column(name = "delete_flg")
	    private boolean deleteFlg;
	
}
