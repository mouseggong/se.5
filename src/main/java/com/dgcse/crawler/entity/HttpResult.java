package com.dgcse.crawler.entity;

import com.sun.istack.internal.Nullable;

public class HttpResult {
    private String body;
    private int code;

    /**
     * Http Result 생성자
     * @param code Http Status Code
     * @param body Http Response Body
     * @see <a href="https://ko.wikipedia.org/wiki/HTTP_%EC%83%81%ED%83%9C_%EC%BD%94%EB%93%9C">Http 상태 코드</a>
     */
    public HttpResult(int code,@Nullable String body){
        this.code = code;
        this.body = body;
    }
    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}