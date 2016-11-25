package com.dgcse.crawler.module;

import com.dgcse.crawler.entity.HttpResult;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Parsing을 수행할 Parser가 상속받아야 하는 기본 클래스
 */

public abstract class BaseParser {
    //Http통신을 수행하기 위한 HttpClient
    private static OkHttpClient httpClient;

    static{
        httpClient = new OkHttpClient.Builder().build();
    }

    /**
     * 제목을 파싱한다.
     * @return 페이지의 제목
     */
    public abstract String getTitle();

    /**
     * 본문을 파싱한다.
     * @return 페이지의 본문
     */
    public abstract String getBody();

    /**
     * 기자 정보를 파싱한다.
     * @return 기자 이름
     */
    public abstract String getReporter();

    /**
     * 페이지가 작성된 날짜를 리턴한다.
     * @return 페이지가 작성된 날짜
     */
    public abstract String getDate();

    /**
     * 해당 기사의 대표사진 url을 추출한다.
     * @return 대표사진 url(String)
     */
    public abstract String getPhoto_url();

    /**
     * 뉴스 페이지 파싱을 시도한다.
     * @param url 파싱할 URL
     * @throws Exception I/O 오류 또는 페이지 자체 오류
     */
    public abstract void parsePage(String url) throws Exception;

    /**
     * 해당 URL이 일반 기사인지 사진기사인지 확인한다.
     */
    public abstract boolean check_NewsType() throws Exception;

    /**
     * Http통신을 수행하여 페이지를 파싱한다
     * @param url Parsing을 수행할 URL
     * @return Http 통신 결과
     * @throws Exception I/O 오류
     */
    public HttpResult parse(String url) throws Exception{
        Request getRequest = new Request.Builder().url(url).build();
        Response response = httpClient.newCall(getRequest).execute();
        int code = response.code();
        String body = response.body().string();
        response.close();
        return new HttpResult(code,body);
    }
}