package jp.co.SurveyMaker.Util;

import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtil {

	/**
	 * 値により、Jsonからキー取得
	 * @param json
	 * @return
	 */
	public static String getKeyByValue(String json, String searchVal) {
		String result = "" ;
		Map<String,Object> jsonMap = convertJsonToMap(json);
		// 引数に値がない場合は、nullを返却する
		if (jsonMap == null ) {
			return result;
		}
		
		for(String key :jsonMap.keySet() ) {
			if(jsonMap.get(key).equals(searchVal)) {
				result = key;
			}
		}
		return result;
	}
	
	/**
	 * JsonからMapへ変換
	 * @param json
	 * @return
	 */
	public static Map<String,Object> convertJsonToMap(String json) {
		Map<String, Object> map = null;
		// 引数に値がない場合は、空文字を返却する
		if (json == null || "".equals(json)) {
			return map;
		}
		
		ObjectMapper mapper = new ObjectMapper();

		try {
			// キーがString、値がObjectのマップに読み込みます。
			map = mapper.readValue(json, new TypeReference<Map<String, Object>>(){});
		} catch (Exception e) {
			e.getStackTrace();
		}
		return map;
	}
}
