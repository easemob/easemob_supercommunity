/*
 * Copyright (c) 2020. Copyright (c) 2020. 中移在线服务有限公司 版权所有
 */

package com.community.easeim.common.http.api;

/**
 * API 响应数据统一封装对象。
 *
 * @author easeMob
 * @date 2020/6/8
 */
public class Result<T> {
    public String code;
    public String msg;
    public T data;
}
