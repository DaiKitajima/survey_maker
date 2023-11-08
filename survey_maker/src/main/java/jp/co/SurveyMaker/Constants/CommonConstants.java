package jp.co.SurveyMaker.Constants;

public class CommonConstants {

	//==========================================================
	// セッションキー
	//==========================================================
	/** ユーザーログイン情報キー */
	public static final String SESSION_KEY_USER_LOGIN = "loginUserKey";
	
	/** リファラ用のキー */
	public static final String SESSION_KEY_REFERER = "refererKey";
	/** リファラ用のキー(2階層深い一覧画面用) */
	public static final String SESSION_KEY_REFERER_LEVEL_TWO = "refererKeyLevelTwo";
	/** リファラ用のキー(3階層深い詳細画面用) */
	public static final String SESSION_KEY_REFERER_LEVEL_THREE = "refererKeyLevelThree";
	/** リファラ用のキー(帳票画面用) */
	public static final String SESSION_KEY_REFERER_REPORT = "refererKeyReport";
	
	//==========================================================
	// 表示
	//==========================================================
	/** 表示数 */
	public static final Integer LIMIT_NUM = 10;
	
	// 共通ファイル種別用デフォルト企業ID
	public static final Integer DEFAULT_COMPANY_ID = 0;
	
	//==========================================================
	// ファイルタイプ
	//==========================================================
	/** ファイル */
	public static final Integer FILE_TYPE_FILE = 1;
	/** フォルダ */
	public static final Integer FILE_TYPE_FOLDER = 2;
	
	//==========================================================
	// 削除フラグ
	//==========================================================
	/** 削除フラグ:削除済み */
	public static final int DELETE_FLAG_ON = 1;
	/** 削除フラグ:未削除 */
	public static final int DELETE_FLAG_OFF = 0;
	
	//==========================================================
	// 公開フラグ
	//==========================================================
	/** 公開フラグ:公開可 */
	public static final int PUBLISH_FLAG_ON = 1;
	/** 公開フラグ:公開不可 */
	public static final int PUBLISH_FLAG_OFF = 0;
	
	//==========================================================
	// CSVファイル出力用情報
	//==========================================================
	/** CSVの項目分割 */
	public static final String CSV_COLUMN_SEPARATOR = ",";
	/** CSVファイルの改行 */
	public static final String CSV_ROW_SEPARATOR = System.lineSeparator();
	
	//==========================================================
	// 区切り符号定義
	//==========================================================
	/** カンマ区切り */
	public static final String COMMA_SEPARATOR = ",";
	/** セミコンロ区切り */
	public static final String SEMICOLON_SEPARATOR = ";";
	/** コンロ区切り */
	public static final String COLON_SEPARATOR = ":";
	
	//==========================================================
	// 画像保存パス使用変数
	//==========================================================
	/** カテゴリーコンテンツ関連画像保存使用 */
	public static final String SAVA_IMG_PATH_CATEGORY = "category";
	/** 質問コンテンツ関連画像保存使用 */
	public static final String SAVA_IMG_PATH_QUESTION = "question";
	/** 質問画像名使用 */
	public static final String SAVA_IMG_NAME_QUESTION = "Question";
	/** 判定点数以上の場合、画像保存使用 */
	public static final String SAVA_IMG_PATH_SUMMARY_ABOVE = "summary_result_above";
	/** 判定点数以下の場合、画像保存使用 */
	public static final String SAVA_IMG_PATH_SUMMARY_BELOW = "summary_result_below";
	/** 質問順番使用 */
	public static final String ORDER_NO_QUESTION = "Question";
	
	//==========================================================
	// 診断軸パターン種別
	//==========================================================
	/** 単数 */
	public static final Integer PARTTERN_SINGULAR = 1;
	/** 複数(ポイント型) */
	public static final Integer PARTTERN_COMPLEX_POINT = 2;
	/** 複数(加算結果型) */
	public static final Integer PARTTERN_COMPLEX_TOTAL = 3;
	/** フロー */
	public static final Integer PARTTERN_FLOW = 4;
}
