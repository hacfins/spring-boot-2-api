/*==============================================================*/
/* Database: edu_account                                        */
/*==============================================================*/
create database edu_account;

use edu_account;

/*==============================================================*/
/* Table: ums_user                                              */
/*==============================================================*/
create table ums_user
(
   id                   int unsigned not null auto_increment,
   user_name            varchar(20) not null comment '用户名',
   nick_name            varchar(20) not null default '' comment '昵称',
   full_name            varchar(20) not null default '' comment '姓名',
   sex                  tinyint not null default 0 comment '性别',
   avator               varchar(255) not null default '' comment '用户头像',
   pcd_id               int not null default 0 comment '行政区划代码',
   company              varchar(128) not null default '' comment '联系地址',
   birthday             date not null default '1970-01-01' comment '生日',
   description          varchar(128) not null default '' comment '描述信息',
   reg_ip               int unsigned not null default 0 comment '注册IP',
   user_type            tinyint not null default 1 comment '用户类型（1 账户、2 设备账户）',
   update_time          datetime not null comment '更新时间',
   delete_time          datetime default NULL comment '删除时间',
   create_time          datetime not null comment '创建时间',
   primary key (id)
)
auto_increment = 1000
engine = InnoDB
default charset = utf8
collate = utf8_general_ci;

alter table ums_user comment '用户信息表';

/*==============================================================*/
/* Table: ums_user_auth                                         */
/*==============================================================*/
create table ums_user_auth
(
   id                   int unsigned not null auto_increment,
   user_name            varchar(20) not null comment '用户名',
   pwd                  varchar(64) not null comment '密码',
   phone                char(15) not null default '' comment '手机号码',
   email                varchar(32) not null default '' comment '邮箱',
   enabled              tinyint not null default 1 comment '状态',
   locked               tinyint not null default 2 comment '锁',
   update_time          datetime not null comment '更新时间',
   delete_time          datetime default NULL comment '删除时间',
   create_time          datetime not null comment '创建时间',
   primary key (id)
)
auto_increment = 1000
engine = InnoDB
default charset = utf8
collate = utf8_general_ci;

alter table ums_user_auth comment '本地授权信息表';

INSERT INTO `ums_user`
VALUES ('1000',
        'admin',
        '超级管理员',
        '超级管理员',
        '3',
        '',
        '0',
        '',
        '1970-01-01',
        '',
        '3232267108',
        '1',
        NOW(),
        NULL,
        NOW());

INSERT INTO `ums_user_auth`
VALUES ('1000',
        'admin',
        '$2a$10$HKtKiX/7zrX9kZvzqtBDL.Yc2HtILtNu11Pd3bQDKtdmcjWpnOIay',
        '',
        '',
        '1',
        '2',
        NOW(),
        NULL,
        NOW());