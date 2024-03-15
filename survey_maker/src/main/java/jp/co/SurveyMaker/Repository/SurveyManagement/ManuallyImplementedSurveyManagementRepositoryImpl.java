package jp.co.SurveyMaker.Repository.SurveyManagement;

import java.util.List;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import jp.co.SurveyMaker.Service.Entity.SurveyManagement;
import jp.co.SurveyMaker.Util.StringUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class ManuallyImplementedSurveyManagementRepositoryImpl implements ManuallyImplementedSurveyManagementRepository {
	
	private final JdbcTemplate jdbcTemplate;
	
	/**
	 * データ検索
	 * @return
	 * @throws Exception 
	 */
	public List<SurveyManagement> surveyContentSearch(SurveyManagement condition) throws Exception{
		
		// データ格納テーブル作成
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT ");
		sql.append("    id                                     ");    // ID 
		sql.append("    , user_id                              ");    // ユーザID 
		sql.append("    , survey_name                          ");    // コンテンツ名 
		sql.append("    , survey_image                         ");    // コンテンツ画像 
		sql.append("    , survey_image_sp                         ");    // コンテンツSP画像 
		sql.append("    , survey_color                         ");    // コンテンツカラー 
		sql.append("    , survey_result_color                         ");    // 診断結果メイン画像背景カラー
		sql.append("    , survey_pattern_id                    ");    // パターンID 
		sql.append("    , survey_header_image                    ");    // コンテンツヘッダ画像
		sql.append("    , survey_description                    ");    // コンテンツ説明
		sql.append("    , survey_induce_area                    ");    // 誘導エリア
		sql.append("    , summary_display_flg                    ");    // 軸評価表示フラグ
		sql.append("    , delete_flg                           ");    // 削除フラグ 
		sql.append("FROM ");
		sql.append("    survey_management ");
		sql.append("WHERE ");
		sql.append("    delete_flg = 0     ");
		
		if(condition != null ) {
			if( condition.getUserId() != null ) {
				sql.append(" and user_id =" + condition.getUserId() );
			}
			if(StringUtil.isNotEmpty(condition.getSurveyName()) ) {
				sql.append(" and survey_name like '%" + condition.getSurveyName() + "%'");
			}
			if( condition.getSurveyPatternId()  != null && condition.getSurveyPatternId()  != -1 ) {
				sql.append(" and survey_pattern_id =" + condition.getSurveyPatternId() );
			}
		} 
		
		RowMapper<SurveyManagement> rm = new BeanPropertyRowMapper<>(SurveyManagement.class);
		List<SurveyManagement> surveyContentLst = null;
		try {
			surveyContentLst = jdbcTemplate.query(sql.toString(), rm);
		} catch(Exception e) {
			log.error("テーブルデータ検索が失敗しました。", e);
			throw e;
		}
		return surveyContentLst;
	}
}
