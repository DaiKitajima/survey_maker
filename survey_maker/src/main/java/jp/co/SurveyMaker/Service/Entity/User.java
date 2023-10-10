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
@SQLDelete(sql="update user set delete_flg=1 where id= ?")
@Table(name = "user")
public class User {
	
	   @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Integer id;

	    @Column(name = "company_id")
	    private Integer companyId;
	    
	    @Column(name = "name")
	    private String name;
	    
	    @Column(name = "login_id")
	    private String loginId;
	    
	    @Column(name = "password")
	    private String password;
	    
	    @Column(name = "mail")
	    private String mail;
	    
	    @Column(name = "delete_flag")
	    private boolean deleteFlg;
	
}
