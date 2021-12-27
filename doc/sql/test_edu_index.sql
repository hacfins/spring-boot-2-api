use test_edu;

/*==============================================================*/
/* Index: ums_role                                              */
/*==============================================================*/
CREATE INDEX idx_ums_role_role_id
    ON ums_role
        (
         role_id(8)
            );

CREATE INDEX idx_ums_role_sort
    ON ums_role
        (
         sort
            );

/*==============================================================*/
/* Index: ums_role_apis                                         */
/*==============================================================*/
CREATE INDEX idx_ums_role_apis_role_id
    ON ums_role_apis
        (
         role_id(8)
            );

CREATE INDEX idx_ums_role_apis_api_id
    ON ums_role_apis
        (
         api_id(8)
            );

/*==============================================================*/
/* Index: ums_api                                               */
/*==============================================================*/
CREATE INDEX idx_ums_api_api_id
    ON ums_api
        (
         api_id(8)
            );

CREATE INDEX idx_ums_api_api_module
    ON ums_api
        (
         api_module
            );

/*==============================================================*/
/* Index: ums_user                                              */
/*==============================================================*/
CREATE UNIQUE INDEX uidx_ums_user_user_name
    ON ums_user
        (
         user_name
            );

/*==============================================================*/
/* Index: ums_user_auth                                         */
/*==============================================================*/
CREATE UNIQUE INDEX uidx_ums_user_auth_user_name
    ON ums_user_auth
        (
         user_name
            );

CREATE INDEX idx_ums_user_auth_phone
    ON ums_user_auth
        (
         phone
            );

CREATE INDEX idx_ums_user_auth_email
    ON ums_user_auth
        (
         email
            );

/*==============================================================*/
/* Index: ums_logs                                              */
/*==============================================================*/
CREATE INDEX idx_ums_logs_op_id
    ON ums_logs
        (
         op_id(8)
            );

CREATE INDEX idx_ums_logs_user_name
    ON ums_logs
        (
         user_name
            );

CREATE INDEX idx_ums_logs_school_id
    ON ums_logs
        (
         school_id(8)
            );

/*==============================================================*/
/* Index: ums_user_login_logs                                   */
/*==============================================================*/
CREATE INDEX idx_ums_user_login_logs_user_name
    ON ums_user_login_logs
        (
         user_name
            );

/*==============================================================*/
/* Index: ums_user_oauths                                         */
/*==============================================================*/
CREATE INDEX idx_ums_user_oauths_oauth_id
    ON ums_user_oauths
        (
         oauth_id(8)
            );

CREATE INDEX idx_ums_user_oauths_user_name
    ON ums_user_oauths
        (
         user_name
            );

/*==============================================================*/
/* Index: ums_user_roles                                        */
/*==============================================================*/
CREATE INDEX idx_ums_user_roles_user_name
    ON ums_user_roles
        (
         user_name
            );

CREATE INDEX idx_ums_user_roles_role_id
    ON ums_user_roles
        (
         role_id(8)
            );

CREATE INDEX idx_ums_user_roles_school_id
    ON ums_user_roles
        (
         school_id(8)
            );

/*==============================================================*/
/* Index: ums_user_tokens                                       */
/*==============================================================*/
CREATE INDEX idx_ums_user_tokens_token_id
    ON ums_user_tokens
        (
         token_id(8)
            );

CREATE INDEX idx_ums_user_tokens_user_name
    ON ums_user_tokens
        (
         user_name
            );

/*==============================================================*/
/* Index: ums_division                                         */
/*==============================================================*/
CREATE INDEX idx_ums_division_parent_id
    ON ums_division
        (
         parent_id
            );

CREATE INDEX idx_ums_division_pcd_path
    ON ums_division
        (
         pcd_path
            );
