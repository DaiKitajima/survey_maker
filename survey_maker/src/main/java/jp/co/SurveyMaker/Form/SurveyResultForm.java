package jp.co.SurveyMaker.Form;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter
@ToString
public class SurveyResultForm {
	
	private Integer id;
	
	private Integer surveyManagementId;
	
	private String key;
	
	// 診断コンテンツ
	private SurveyContentUpdateForm survey;
	
	// 総合評価所属のカテゴリーID
	private Integer topCategoryId;
	// 総合評価タイトル
    private String surveySummaryTitle;
    // 総合評価画像
    private String surveySummaryImageBase64;
    // 総合評価詳細
    private String surveySummaryDetail;
    
	// 診断軸結果リスト
	private List<CategoryResultForm> categoryResultLst;
    
}
