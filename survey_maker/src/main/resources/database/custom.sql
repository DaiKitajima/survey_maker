alter table survey_management add column survey_header_image varchar(256) comment 'コンテンツ画像ヘッダ' after survey_pattern_id;
alter table survey_management add column survey_description text comment 'コンテンツ説明' after survey_header_image;
alter table survey_management add column survey_induce_area text comment '誘導エリア' after survey_description;
alter table survey_management add column summary_display_flg tinyint(1) comment '軸評価表示フラグ' after survey_induce_area;

alter table survey_category add column survey_summary_induce_above text comment '総合評価誘導エリア（判定点数以上）' after survey_summary_detail_above;
alter table survey_category add column survey_summary_induce_below text comment '総合評価誘導エリア（判定点数以下）' after survey_summary_detail_below;

alter table survey_management add column survey_result_color varchar(256) comment '診断結果メイン画像背景カラー' after survey_color;

alter table survey_management add column survey_image_sp varchar(256) comment 'コンテンツ画像SP' after survey_image;
alter table survey_management add column survey_result_color varchar(256) comment '診断結果メイン画像背景カラー' after survey_color;