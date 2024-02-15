create table IF NOT EXISTS `user` (
`id` int NOT NULL AUTO_INCREMENT COMMENT 'ID',
`company_id` int COMMENT '所属企業コード',
`name` varchar(128) COMMENT '名前',
`login_id` varchar(128) COMMENT 'ログインID',
`password` varchar(128) COMMENT 'パスワード',
`mail` varchar(256) COMMENT 'メールアドレス',
`delete_flg` tinyint(1) NOT NULL COMMENT '削除フラグ',
PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8 COMMENT='ユーザ管理テーブル';

create table IF NOT EXISTS `m_company` (
`id` int NOT NULL AUTO_INCREMENT COMMENT 'ID',
`company_name` varchar(256) COMMENT '企業名',
`postal_code` varchar(50) COMMENT '郵便番号',
`address` varchar(256) COMMENT '住所（所在地）',
`tel` varchar(50) COMMENT '電話番号',
`representative_name` varchar(256) COMMENT '代表者の氏名',
`delete_flg` tinyint(1) NOT NULL COMMENT '削除フラグ',
PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8 COMMENT='企業マスタ';

create table IF NOT EXISTS `m_survey_pattern` (
`id` int NOT NULL AUTO_INCREMENT COMMENT 'ID',
`survey_pattern_name` varchar(256) COMMENT 'パターン名',
`delete_flg` tinyint(1) NOT NULL COMMENT '削除フラグ',
PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8 COMMENT='診断コンテンツパターンマスタ';

create table IF NOT EXISTS `survey_management` (
`id` int NOT NULL AUTO_INCREMENT COMMENT 'ID',
`user_id` int COMMENT 'ユーザID',
`survey_name` varchar(256) COMMENT 'コンテンツ名',
`survey_image` varchar(256) COMMENT 'コンテンツ画像',
`survey_color` varchar(256) COMMENT 'コンテンツカラー',
`survey_pattern_id` int COMMENT 'パターンID',
`survey_content_header_image` varchar(256) COMMENT 'コンテンツ画像ヘッダ',
`survey_content_description` text COMMENT 'コンテンツ説明',
`survey_induce_area` text COMMENT '誘導エリア',
`summary_display_flg` tinyint(1) COMMENT '軸評価非表示フラグ',
`delete_flg` tinyint(1) NOT NULL COMMENT '削除フラグ',
PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8 COMMENT='診断コンテンツ管理テーブル';

create table IF NOT EXISTS `survey_category` (
`id` int NOT NULL AUTO_INCREMENT COMMENT 'ID',
`survey_management_id` int COMMENT '診断コンテンツID',
`survey_category_name` varchar(256) COMMENT '診断軸名',
`survey_category_color` varchar(256) COMMENT '診断軸カラー',
`survey_summary_decide_point` int COMMENT '総合評価判定点数',
`survey_summary_title_above` varchar(256) COMMENT '総合評価タイトル（判定点数以上）',
`survey_summary_image_above` varchar(256) COMMENT '総合評価画像（判定点数以上）',
`survey_summary_detail_above` text COMMENT '総合評価詳細（判定点数以上）',
`survey_summary_induce_above` text COMMENT '総合評価誘導エリア（判定点数以上）',
`survey_summary_title_below` varchar(256) COMMENT '総合評価タイトル（判定点数以下）',
`survey_summary_image_below` varchar(256) COMMENT '総合評価画像（判定点数以下）',
`survey_summary_detail_below` text COMMENT '総合評価詳細（判定点数以下）',
`survey_summary_induce_below` text COMMENT '総合評価誘導エリア（判定点数以下）',
`survey_category_content` json COMMENT '診断軸コンテンツ',
`survey_category_position` json COMMENT 'フローチャート軸位置',
`delete_flg` tinyint(1) NOT NULL COMMENT '削除フラグ',
PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8 COMMENT='診断軸コンテンツテーブル';

create table IF NOT EXISTS `survey_summary_result` (
`id` int NOT NULL AUTO_INCREMENT COMMENT 'ID',
`survey_management_id` int COMMENT '診断コンテンツID',
`survey_summary_title` varchar(256) COMMENT '総合評価タイトル',
`survey_summary_text` text COMMENT '総合評価テキスト',
`survey_summary_image` varchar(256) COMMENT '総合評価画像',
`survey_summary_from` varchar(50) COMMENT '総合評価範囲From',
`survey_summary_to` varchar(50) COMMENT '総合評価範囲To',
`delete_flg` tinyint(1) NOT NULL COMMENT '削除フラグ',
PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8 COMMENT='総合評価結果コンテンツテーブル';

create table IF NOT EXISTS `survey_question` (
`id` int NOT NULL AUTO_INCREMENT COMMENT 'ID',
`survey_management_id` int COMMENT '診断コンテンツID',
`question_order_no` int COMMENT '質問順番',
`question_title` varchar(256) COMMENT '質問内容',
`question_image` varchar(256) COMMENT '質問画像 ',
`question_position` json COMMENT '質問位置',
`answer_content` json COMMENT '回答コンテンツ',
`delete_flg` tinyint(1) NOT NULL COMMENT '削除フラグ',
PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8 COMMENT='診断質問コンテンツテーブル';

create table IF NOT EXISTS `survey_question_link` (
`id` int NOT NULL AUTO_INCREMENT COMMENT 'ID',
`survey_management_id` int COMMENT '診断コンテンツID',
`survey_question_id` int COMMENT '質問コンテンツID',
`answer_id` int COMMENT '回答ID',
`link_type` int COMMENT 'リンク種別　',
`link_to` int COMMENT 'リンク先',
`delete_flg` tinyint(1) NOT NULL COMMENT '削除フラグ',
PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8 COMMENT='診断質問リンクコンテンツテーブル';

create table IF NOT EXISTS `survey_result` (
`id` int NOT NULL AUTO_INCREMENT COMMENT 'ID',
`survey_key` varchar(50) COMMENT '独自キー',
`survey_management_id` int COMMENT '診断コンテンツID',
`summary_result_content` json DEFAULT NULL COMMENT '総合評価結果コンテンツ',
`survey_result_content` json DEFAULT NULL COMMENT '診断結果コンテンツ',
`expire_date` datetime DEFAULT NULL COMMENT '有効期限',
`delete_flg` tinyint(1) NOT NULL COMMENT '削除フラグ',
PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8 COMMENT='診断結果管理テーブル';

--　初期マスタデータ
-- システム管理者：kitajima@w-bridge.co.jp：webridge
DELETE FROM surveymaker.`user` WHERE id = 1;
insert into surveymaker.`user`(id, company_id,name,login_id,password,mail,delete_flg) values (1, 1,'システム管理者','kitajima@w-bridge.co.jp','1JASMdMbrT5S0fa0qLWJxAbb','kitajima@w-bridge.co.jp','0');

-- 診断パターンマスタ
DELETE FROM surveymaker.m_survey_pattern WHERE id = 1;
DELETE FROM surveymaker.m_survey_pattern WHERE id = 2;
DELETE FROM surveymaker.m_survey_pattern WHERE id = 3;
DELETE FROM surveymaker.m_survey_pattern WHERE id = 4;
insert into surveymaker.m_survey_pattern(id, survey_pattern_name,delete_flg) values
    (1, '単数','0')
  , (2, '複数（ポイント型）','0')
  , (3, '複数（加算結果型）','0')
  , (4, 'フロー','0');