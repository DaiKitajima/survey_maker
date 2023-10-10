package jp.co.SurveyMaker.Repository.Common;

/**
 * 共通操作を持つ断片インターフェース
 * @author s.yamazaki
 *
 */
public interface CommonRepository {

	/**
	 * 最終登録ID取得
	 * @return
	 */
	public Integer getLastInsertId();

	/**
	 * 直前に発行されたSQLの総取得件数を取得する
	 * Limit句で取得件数に制限をかけている場合に使用する
	 * @return
	 */
	public Integer getTotalDataCountForLimit();


	
}