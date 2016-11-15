package com.dgcse.crawler.entity;

import com.sun.istack.internal.Nullable;

/**
 * Created by LeeHyungRae on 2016. 11. 14..
 *
 * Http Response 결과를 저장하기 위한 클래스
 */
public class HttpResult {
    int code;
    String body;

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

    public int getCode(){
        return code;
    }

    public String getBody(){
        return body;
    }
}
