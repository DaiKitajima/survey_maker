CREATE TABLE IF NOT EXISTS `user` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `company_id` int(11) DEFAULT NULL COMMENT '所属企業コード',
  `name` varchar(128) DEFAULT NULL COMMENT '名前',
  `login_id` varchar(50) DEFAULT NULL COMMENT 'ログインID',
  `password` varchar(128) DEFAULT NULL COMMENT 'パスワード',
  `mail` varchar(256) DEFAULT NULL COMMENT 'メールアドレス',
  `delete_flag` tinyint(1) DEFAULT NULL COMMENT '削除フラグ',
  PRIMARY KEY (`id`)
);