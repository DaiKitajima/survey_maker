package jp.co.SurveyMaker.Repository.Common;

import org.springframework.jdbc.core.JdbcTemplate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class CommonRepositoryImpl implements CommonRepository {
	private final JdbcTemplate jdbcTemplate;

	/**
	 * 最終登録ID取得
	 * @return
	 */
	@Override
	public Integer getLastInsertId() {

		String sql = "SELECT LAST_INSERT_ID()";

		Integer lastInsertId = jdbcTemplate.queryForObject(sql.toString(), Integer.class);

		return lastInsertId;
	}

	/**
	 * 直前に発行されたSQLの総取得件数を取得する
	 * Limit句で取得件数に制限をかけている場合に使用する
	 * @return
	 */
	@Override
	public Integer getTotalDataCountForLimit() {

		String sql = "SELECT FOUND_ROWS() as totalDataCount";

		Integer totalDataCount = jdbcTemplate.queryForObject(sql.toString(), Integer.class);
		
		return totalDataCount;
	}
}
