/*==============================================================*/
/* Database name:  test_edu                                     */
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     2021/12/27 21:44:23                          */
/*==============================================================*/


/*==============================================================*/
/* Database: test_edu                                           */
/*==============================================================*/
create database test_edu;

use test_edu;

/*==============================================================*/
/* Table: ums_api                                               */
/*==============================================================*/
create table ums_api
(
   id                   int unsigned not null auto_increment,
   api_id               char(32) not null comment '权限id号',
   api_name             varchar(32) not null comment '名称',
   api_module           varchar(24) not null comment '模块组',
   api_url              varchar(256) not null comment '地址',
   update_time          datetime not null comment '更新时间',
   delete_time          datetime default NULL comment '删除标记',
   create_time          datetime not null comment '创建时间',
   primary key (id)
)
auto_increment = 1000
engine = InnoDB
default charset = utf8
collate = utf8_general_ci;

alter table ums_api comment '权限表';

/*==============================================================*/
/* Table: ums_division                                          */
/*==============================================================*/
create table ums_division
(
   pcd_id               int unsigned not null auto_increment comment '省市区id号',
   pcd_name             varchar(20) not null comment '名称',
   pcd_path             varchar(40) not null comment '路径（A/B/C）',
   parent_id            int unsigned not null default 0 comment '父节点id号',
   level                tinyint unsigned not null default 1 comment '层级',
   sort                 int unsigned not null comment '排序值',
   update_time          datetime not null comment '更新时间',
   delete_time          datetime default NULL comment '删除时间',
   create_time          datetime not null comment '创建时间',
   primary key (pcd_id)
)
auto_increment = 1000
engine = InnoDB
default charset = utf8
collate = utf8_general_ci;

alter table ums_division comment '省市区表';

/*==============================================================*/
/* Table: ums_logs                                              */
/*==============================================================*/
create table ums_logs
(
   id                   int unsigned not null auto_increment,
   op_id                char(32) not null comment '操作id号',
   user_name            varchar(20) not null comment '用户名',
   school_id            char(32) not null default '' comment '机构id号（系统也理解为一个机构）',
   op_url               varchar(64) not null default '' comment '操作的url（请求的URL地址）',
   op_comment           varchar(255) not null default '' comment '操作说明',
   op_params            text not null default '' comment '操作的参数',
   op_result            int not null default 1 comment '操作结果（操作成功/操作失败/操作异常等）',
   use_time             int not null default 0 comment '耗时（微秒）',
   location             varchar(48) not null default '' comment '操作地点（济南）',
   ip                   int unsigned not null default 0 comment '操作ip',
   os_name              varchar(24) not null default '' comment '操作系统（Windows 10, MacOS 10）',
   browse_name          varchar(24) not null default '' comment '浏览器（如 Chrome 87）',
   update_time          datetime not null comment '更新时间',
   delete_time          datetime default NULL comment '删除时间',
   create_time          datetime not null comment '操作时间',
   primary key (id)
)
auto_increment = 1000
engine = InnoDB
default charset = utf8
collate = utf8_general_ci;

alter table ums_logs comment '操作日志表';

/*==============================================================*/
/* Table: ums_role                                              */
/*==============================================================*/
create table ums_role
(
   id                   int unsigned not null auto_increment,
   role_id              char(32) not null comment '角色id号',
   role_name            varchar(20) not null comment '角色名称',
   view_system          tinyint not null default 2 comment '访问后台管理（1是、2否）',
   view_school          tinyint not null default 2 comment '访问机构管理（1是、2否）',
   is_system            tinyint not null default 1 comment '是否是系统类型(1，是；2，否)',
   role_type            tinyint not null default 1 comment '角色类型（内置角色（不可删除）、普通角色）',
   sort                 smallint not null default 99 comment '排序字段',
   description          varchar(128) not null default '' comment '描述信息',
   update_time          datetime not null comment '更新时间',
   delete_time          datetime default NULL comment '删除标记',
   create_time          datetime not null comment '创建时间',
   primary key (id)
)
auto_increment = 1000
engine = InnoDB
default charset = utf8
collate = utf8_general_ci;

alter table ums_role comment '角色表';

/*==============================================================*/
/* Table: ums_role_apis                                         */
/*==============================================================*/
create table ums_role_apis
(
   id                   int unsigned not null auto_increment,
   role_id              char(32) not null comment '角色id号',
   api_id               char(32) not null comment '权限id号',
   update_time          datetime not null comment '更新时间',
   delete_time          datetime default NULL comment '删除时间',
   create_time          datetime not null comment '创建时间',
   primary key (id)
)
auto_increment = 1000
engine = InnoDB
default charset = utf8
collate = utf8_general_ci;

alter table ums_role_apis comment '角色权限映射表';

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

/*==============================================================*/
/* Table: ums_user_login_logs                                   */
/*==============================================================*/
create table ums_user_login_logs
(
   id                   int unsigned not null auto_increment,
   user_name            varchar(20) not null comment '用户名',
   location             varchar(48) not null default '' comment '操作地点（济南）',
   ip                   int unsigned not null default 0 comment '操作ip',
   os_name              varchar(24) not null default '' comment '操作系统（Windows 10, MacOS 10）',
   browse_name          varchar(24) not null default '' comment '浏览器（如 Chrome 87）',
   update_time          datetime not null comment '更新时间',
   delete_time          datetime default NULL comment '删除时间',
   create_time          datetime not null comment '登录时间',
   primary key (id)
)
auto_increment = 1000
engine = InnoDB
default charset = utf8
collate = utf8_general_ci;

alter table ums_user_login_logs comment '用户登录表';

/*==============================================================*/
/* Table: ums_user_oauths                                       */
/*==============================================================*/
create table ums_user_oauths
(
   id                   int unsigned not null auto_increment,
   oauth_id             char(32) not null comment '第三方应用的唯一标识',
   user_name            varchar(20) not null comment '用户名',
   oauth_type           tinyint not null default 1 comment '第三方应用类型',
   update_time          datetime not null comment '更新时间',
   delete_time          datetime default NULL comment '删除时间',
   create_time          datetime not null comment '创建时间',
   primary key (id)
)
auto_increment = 1000
engine = InnoDB
default charset = utf8
collate = utf8_general_ci;

alter table ums_user_oauths comment '第三方授权信息表';

/*==============================================================*/
/* Table: ums_user_roles                                        */
/*==============================================================*/
create table ums_user_roles
(
   id                   int unsigned not null auto_increment,
   user_name            varchar(20) not null comment '用户名',
   role_id              char(32) not null comment '角色id号',
   school_id            char(32) not null default '' comment '机构id号（系统也理解为一个机构）',
   update_time          datetime not null comment '更新时间',
   delete_time          datetime default NULL comment '删除时间',
   create_time          datetime not null comment '创建时间',
   primary key (id)
)
auto_increment = 1000
engine = InnoDB
default charset = utf8
collate = utf8_general_ci;

alter table ums_user_roles comment '用户角色映射表';

/*==============================================================*/
/* Table: ums_user_tokens                                       */
/*==============================================================*/
create table ums_user_tokens
(
   id                   int unsigned not null auto_increment,
   token_id             char(32) not null comment '授权的token',
   user_name            varchar(20) not null comment '用户名',
   expire               datetime not null comment '过期时间',
   os_type              tinyint not null default 1 comment '登录平台类型（PC\Mobile\平板）',
   status               tinyint not null default 1 comment '状态',
   update_time          datetime not null comment '更新时间',
   delete_time          datetime default NULL comment '删除标记',
   create_time          datetime not null comment '创建时间',
   primary key (id)
)
auto_increment = 1000
engine = InnoDB
default charset = utf8
collate = utf8_general_ci;

alter table ums_user_tokens comment '登录信息表';

