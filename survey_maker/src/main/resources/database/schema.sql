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
`delete_flg` tinyint(1) NOT NULL COMMENT '削除フラグ',
PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8 COMMENT='診断コンテンツ管理テーブル';

create table IF NOT EXISTS `survey_category` (
`id` int NOT NULL AUTO_INCREMENT COMMENT 'ID',
`survey_management_id` int COMMENT '診断コンテンツID',
`survey_category_name` varchar(256) COMMENT '診断軸名',
`survey_summary_decide_point` varchar(50) COMMENT '総合評価判定点数',
`survey_summary_title_above` varchar(256) COMMENT '総合評価タイトル（判定点数以上）',
`survey_summary_image_above` varchar(50) COMMENT '総合評価画像（判定点数以上）',
`survey_summary_detail_above` text COMMENT '総合評価詳細（判定点数以上）',
`survey_summary_title_below` varchar(256) COMMENT '総合評価タイトル（判定点数以下）',
`survey_summary_image_below` varchar(50) COMMENT '総合評価画像（判定点数以下）',
`survey_summary_detail_below` text COMMENT '総合評価詳細（判定点数以下）',
`survey_category_content` json COMMENT '診断軸コンテンツ',
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
`link_to` varchar(50) COMMENT 'リンク先',
`delete_flg` tinyint(1) NOT NULL COMMENT '削除フラグ',
PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8 COMMENT='診断質問リンクコンテンツテーブル';

create table IF NOT EXISTS `survey_result` (
`id` int NOT NULL AUTO_INCREMENT COMMENT 'ID',
`key` varchar(50) COMMENT '独自キー',
`survey_management_id` int COMMENT '診断コンテンツID',
`survey_summary_result_id` int COMMENT '総合評価結果コンテンツID',
`result_content` json COMMENT '結果コンテンツ',
`expire_date` datetime COMMENT '有効期限',
`delete_flg` tinyint(1) NOT NULL COMMENT '削除フラグ',
PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8 COMMENT='診断結果管理テーブル';

