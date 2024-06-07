package com.ispengya.shortlink.common.errorcode;

/**
 * 基础错误码定义
 */
public enum BaseErrorCode implements IErrorCode {

    // ========== 一级宏观错误码 客户端错误 ==========
    CLIENT_ERROR("A000001", "用户端错误"),

    // ========== 二级宏观错误码 用户注册错误 ==========
    USER_REGISTER_ERROR("A000100", "用户注册错误"),
    USER_NAME_VERIFY_ERROR("A000101", "用户名校验失败"),
    USER_NAME_EXIST_ERROR("A000102", "用户名已存在"),
    USER_NAME_SENSITIVE_ERROR("A000103", "用户名包含敏感词"),
    USER_NAME_SPECIAL_CHARACTER_ERROR("A000104", "用户名包含特殊字符"),
    PASSWORD_VERIFY_ERROR("A000105", "密码校验失败"),
    PASSWORD_SHORT_ERROR("A000106", "密码长度不够"),
    PHONE_VERIFY_ERROR("A000107", "手机格式校验失败"),


    //===================二级宏观错误码 用户查询错误=======================
    USER_NOT_EXIST("A000201","用户不存在"),

    // ========== 二级宏观错误码 系统请求缺少幂等Token ==========
    IDEMPOTENT_TOKEN_NULL_ERROR("A000300", "幂等Token为空"),
    IDEMPOTENT_TOKEN_DELETE_ERROR("A000301", "幂等Token已被使用或失效"),




    // ========== 一级宏观错误码 系统执行出错 ==========
    SERVICE_ERROR("B000001", "系统执行出错"),
    // ========== 二级宏观错误码 系统执行超时 ==========
    SERVICE_TIMEOUT_ERROR("B000100", "系统执行超时"),
    SERVICE_PARAM_ERROR("B000200","参数错误"),



    // ========== 一级宏观错误码 调用第三方服务出错 ==========
    REMOTE_ERROR("C000001", "调用第三方服务出错");

    private final String code;

    private final String message;

    BaseErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String code() {
        return code;
    }

    @Override
    public String message() {
        return message;
    }
}
