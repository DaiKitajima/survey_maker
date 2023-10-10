package jp.co.SurveyMaker.Util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Calendar;
import java.util.Date;

public class DateTimeUtil {
	
	/** 【yyyy-MM-dd】フォーマット文字列 */
	public static final String YYYYMMDD_HYPHEN = "yyyy-MM-dd";
	/** 【yyyy-MM-dd】フォーマット文字列 */
	public static final String YYYYMMDD_SLASH = "yyyy/MM/dd";
	/** 【yyyy-MM-dd HH:mm】フォーマット文字列 */
	public static final String YYYYMMDD_HHMM_SLASH = "yyyy/MM/dd HH:mm";
	/** 【yyyy-MM-dd HH:mm:ss】フォーマット文字列 */
	public static final String YYYYMMDD_HHMMSS_SLASH = "yyyy/MM/dd HH:mm:ss";
	
	//日付見た目変更メソッド
	public String dateConvert(LocalDate d) {
		String s = new SimpleDateFormat("yyyy/MM/dd").format(d);
		return s;
	}
	
	//日時見た目変更メソッド
	public String dateTimeConvert(LocalDateTime ldt) {
		String s = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(ldt);
		return s;
	}

	//日時見た目変更メソッド
	public static String getDateTimeForyyyyMMddHHmmss() {
		Date ldt = new Date();
		String s = new SimpleDateFormat("yyyyMMddHHmmss").format(ldt);
		return s;
	}
	
	/**
	 * システム日付を取得する Date型
	 *
	 * @return
	 */
	public static Date getSystemDateTime() {
		Calendar cal = Calendar.getInstance();
		return cal.getTime();
	}

	/**
	 * システム日付を取得する String型
	 *
	 * @param format
	 * @return
	 */
	public static String getSystemDateTimeStr(String format) {
		Date sysDate = getSystemDateTime();
		return convertDate(sysDate, format);
	}
	
	/**
	 * Date型をString型に変換する
	 *
	 * @param targetDate
	 * @param format
	 * @return
	 */
	public static String convertDate(Date targetDate, String format) {

		String sysDateStr = "";

		try {
			DateFormat df = new SimpleDateFormat(format);
			sysDateStr = df.format(targetDate);

		} catch(Exception e) {
			// フォーマットエラーは空文字返却し、例外を握りつぶす
		}

		return sysDateStr;
	}
	
	/**
	 * String型をDate型に変換する
	 *
	 * @param targetDate
	 * @param format
	 * @return
	 */
	public static Date parseDate(String targetDate, String format) {

		Date sysDateStr = null;

		try {
			DateFormat df = new SimpleDateFormat(format);
			sysDateStr = df.parse(targetDate);

		} catch(Exception e) {
			// フォーマットエラーは空文字返却し、例外を握りつぶす
		}
		return sysDateStr;
	}
	
	// 日付判定
	public static boolean isDate(String input, String format) {
	    try {
	        LocalDate.parse(input, DateTimeFormatter.ofPattern(format));
	        return true;
	    } catch (DateTimeParseException e) {
	        return false;
	    }
	}
	
}
