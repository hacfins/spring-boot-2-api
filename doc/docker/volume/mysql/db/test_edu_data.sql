use test_edu;


/*==============================================================*/
/* Database: test_edu                                        */
/*==============================================================*/
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


/*==============================================================*/
/* 目前菜单与 API 合二为一管理
 *
 * 菜单管理 id 从 1000-5000
 *         model 以 menu_ 开头
 */
/*==============================================================*/
/*
 * ---------------------------  系统菜单  -------------------------
 */
INSERT INTO `ums_api`
VALUES (1001, "10010d3a5c07472e9bc30dfefbaba851", "访问控制", "menu_system", "/menu/admin/visitor",
        NOW(), NULL, NOW());
INSERT INTO `ums_api`
VALUES (1003, "10030d3a5c07472e9bc30dfefbaba851", "用户管理", "menu_system", "/menu/admin/user",
        NOW(), NULL, NOW());
INSERT INTO `ums_api`
VALUES (1004, "10040d3a5c07472e9bc30dfefbaba851", "权限管理", "menu_system", "/menu/admin/role",
        NOW(), NULL, NOW());
INSERT INTO `ums_api`
VALUES (1014, "10140d3a5c07472e9bc30dfefbaba851", "系统日志", "menu_system", "/menu/admin/systemlog",
        NOW(), NULL, NOW());

/*
 * ---------------------------  前台菜单  ---------------------------
 */
INSERT INTO `ums_api`
VALUES (3001, "30010d3a5c07472e9bc30dfefbaba851", "个人信息", "menu_portal", "/menu/portal/user/info",
        NOW(), NULL, NOW());
INSERT INTO `ums_api`
VALUES (3002, "30020d3a5c07472e9bc30dfefbaba851", "账号绑定", "menu_portal", "/menu/portal/user/setbindsns",
        NOW(), NULL, NOW());
INSERT INTO `ums_api`
VALUES (3003, "30030d3a5c07472e9bc30dfefbaba851", "操作记录", "menu_portal", "/menu/portal/user/oplog",
        NOW(), NULL, NOW());

/*==============================================================*/
/* API 后台管理
 *
 * id 从 5001 开始
 */
/*==============================================================*/
/*
 * ---------------------------  auth-role  ---------------------------
 */
INSERT INTO `ums_api`
VALUES (5001, "50010d3a5c07472e9bc30dfefbaba851", "角色信息", "auth_role", "/admin/auth/role/info", NOW(), NULL,
        NOW());
INSERT INTO `ums_api`
VALUES (5002, "50020d3a5c07472e9bc30dfefbaba851", "角色列表", "auth_role", "/admin/auth/role/get_list", NOW(), NULL, NOW());
INSERT INTO `ums_api`
VALUES (5003, "50030d3a5c07472e9bc30dfefbaba851", "创建角色", "auth_role", "/admin/auth/role/add", NOW(), NULL, NOW());
INSERT INTO `ums_api`
VALUES (5004, "50040d3a5c07472e9bc30dfefbaba851", "修改角色", "auth_role", "/admin/auth/role/modify", NOW(), NULL, NOW());
INSERT INTO `ums_api`
VALUES (5005, "50050d3a5c07472e9bc30dfefbaba851", "移动角色", "auth_role", "/admin/auth/role/move_to", NOW(), NULL, NOW());
INSERT INTO `ums_api`
VALUES (5006, "50060d3a5c07472e9bc30dfefbaba851", "角色名是否存在", "auth_role", "/admin/auth/role/exist_name", NOW(), NULL,
        NOW());
INSERT INTO `ums_api`
VALUES (5007, "50070d3a5c07472e9bc30dfefbaba851", "删除角色", "auth_role", "/admin/auth/role/del", NOW(), NULL, NOW());

/*
 * ---------------------------  auth-auth  ---------------------------
 */
INSERT INTO `ums_api`
VALUES (5101, "51010d3a5c07472e9bc30dfefbaba851", "权限列表", "auth_auth", "/admin/auth/auth/get_list", NOW(), NULL, NOW());
INSERT INTO `ums_api`
VALUES (5102, "51020d3a5c07472e9bc30dfefbaba851", "修改角色权限", "auth_auth", "/admin/auth/auth/set_rules", NOW(), NULL,
        NOW());

/*
 * ---------------------------  auth-user  ---------------------------
 */
INSERT INTO `ums_api`
VALUES (5201, "52010d3a5c07472e9bc30dfefbaba851", "用户列表", "auth_user", "/admin/auth/user/get_list", NOW(), NULL, NOW());
INSERT INTO `ums_api`
VALUES (5202, "52020d3a5c07472e9bc30dfefbaba851", "设置用户系统角色", "auth_user", "/admin/auth/user/set_roles", NOW(), NULL,
        NOW());
INSERT INTO `ums_api`
VALUES (5204, "52040d3a5c07472e9bc30dfefbaba851", "添加用户", "auth_user", "/admin/auth/user/add", NOW(), NULL, NOW());
INSERT INTO `ums_api`
VALUES (5205, "52050d3a5c07472e9bc30dfefbaba851", "修改用户", "auth_user", "/admin/auth/user/modify", NOW(), NULL, NOW());
INSERT INTO `ums_api`
VALUES (5206, "52060d3a5c07472e9bc30dfefbaba851", "重置密码", "auth_user", "/admin/auth/user/reset_pwd", NOW(), NULL,
        NOW());
INSERT INTO `ums_api`
VALUES (5207, "52070d3a5c07472e9bc30dfefbaba851", "启用禁用用户", "auth_user", "/admin/auth/user/enabled", NOW(), NULL,
        NOW());



/*==============================================================*/
/*
 * ---------------------------  monitor-log  ---------------------------
 */
INSERT INTO `ums_api`
VALUES (8001, "80010d3a5c07472e9bc30dfefbaba851", "登录日志", "monitor_log", "/admin/monitor/log/login", NOW(), NULL,
        NOW());
INSERT INTO `ums_api`
VALUES (8002, "80020d3a5c07472e9bc30dfefbaba851", "系统操作日志", "monitor_log", "/admin/monitor/log/system", NOW(), NULL,
        NOW());



/*==============================================================//
 * ---------------------------  角色  ---------------------------
 *==============================================================*/
#系统角色
INSERT INTO `ums_role`
VALUES ('1001', '4ba77b3d79ff4007bbf2b6a92bde35ee', '普通用户', '2', '2', '1', '1', '1001', '',
        NOW(), null, NOW());


#普通用户
INSERT INTO `ums_role_apis` (`role_id`, `api_id`, `update_time`, `delete_time`, `create_time`)VALUES ('4ba77b3d79ff4007bbf2b6a92bde35ee', '80010d3a5c07472e9bc30dfefbaba851', NOW(), null,
                                    NOW());
INSERT INTO `ums_role_apis` (`role_id`, `api_id`, `update_time`, `delete_time`, `create_time`)VALUES ('4ba77b3d79ff4007bbf2b6a92bde35ee', '30020d3a5c07472e9bc30dfefbaba851', NOW(), null,
                                                                                                      NOW());
INSERT INTO `ums_role_apis` (`role_id`, `api_id`, `update_time`, `delete_time`, `create_time`)VALUES ('4ba77b3d79ff4007bbf2b6a92bde35ee', '30010d3a5c07472e9bc30dfefbaba851', NOW(), null,
                                                                                                      NOW());
INSERT INTO `ums_role_apis` (`role_id`, `api_id`, `update_time`, `delete_time`, `create_time`)VALUES ('4ba77b3d79ff4007bbf2b6a92bde35ee', '30030d3a5c07472e9bc30dfefbaba851', NOW(), null,
                                                                                                      NOW());
