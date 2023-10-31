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
@SQLDelete(sql="update survey_question_link set delete_flg=1 where id= ?")
@Table(name = "survey_question_link")
public class SurveyQuestionLink {
	
	   @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Integer id;

	    @Column(name = "survey_management_id")
	    private Integer surveyManagementId;
	    
	    @Column(name = "survey_question_id")
	    private Integer surveyQuestionId;
	    
	    @Column(name = "answer_id")
	    private String answerId;
	    
	    @Column(name = "link_type")
	    private Integer linkType;
	    
	    @Column(name = "link_to")
	    private String  linkTo;
	    
	    @Column(name = "delete_flg")
	    private boolean deleteFlg;
	
}
