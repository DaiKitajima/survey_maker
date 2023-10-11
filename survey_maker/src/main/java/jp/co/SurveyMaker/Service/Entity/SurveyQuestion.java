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
@SQLDelete(sql="update survey_question set delete_flg=1 where id= ?")
@Table(name = "survey_question")
public class SurveyQuestion {
	
	   @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Integer id;

	    @Column(name = "survey_management_id")
	    private Integer surveyManagementId;
	    
	    @Column(name = "question_order_no")
	    private String questionOrderNo;
	    
	    @Column(name = "question_title")
	    private String questionTitle;
	    
	    @Column(name = "question_image")
	    private String questionImage;
	    
	    @Column(name = "answer_content")
	    private String answerContent;
	    
	    @Column(name = "delete_flg")
	    private boolean deleteFlg;
	
}
