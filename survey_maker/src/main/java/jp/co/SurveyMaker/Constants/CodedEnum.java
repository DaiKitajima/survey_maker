package jp.co.SurveyMaker.Constants;

/**
 * JDBCの変換処理にて「Enum<->Integer」する場合に当該クラスを継承すること。
 * @author s.yamazaki
 *
 */
public interface CodedEnum {
	
	public Integer getCode();

}
