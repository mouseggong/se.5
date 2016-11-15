package com.dgcse.crawler;
import com.dgcse.crawler.entity.HttpResult;
import org.junit.Ignore;
import org.junit.Test;
import java.util.List;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class SegyeParserTest {

    @Test
    //@Ignore
    public void test() throws Exception{//2016년 10월 03일의 기사들의 URL주소를 가져오는 메소드
        SegyeParser parser = new SegyeParser();
        HttpResult httpResult = parser.getDaily("2016","10","03",1);//파서로 해당 날짜의 httpResult를 생성

        List<String> urlList = parser.getNewsUrlList(httpResult);//파서로 해당 날짜의 httpResult의 뉴스 URL리스트를 생성
        //for(String str : urlList){
        //System.out.println(httpResult);
        //}
        for(String str : urlList){
        System.out.println(str);
    }
}
    @Test
    @Ignore
    public void testCrawler() throws Exception{//세계파서로 해당 URL의 모든 소스내용을 파싱하여 가져온다.
        SegyeParser segyeparser = new SegyeParser();
        String result = segyeparser.parse("http://www.segye.com/content/html/2016/10/07/20161007002895.html");
        //assertNotNull(result);
        System.out.println(result);
    }
    @Test
    @Ignore
    public void testParseTitle() throws Exception{//해당 URL의 제목부분만 가져온다.
        SegyeParser segyeparser = new SegyeParser();
        String body = segyeparser.parse("http://www.segye.com/content/html/2016/10/07/20161007002895.html");
        String title = segyeparser.getTitle(body);//본문의 전체코드에서 Title에 해당하는 String을 가져온다.
        //assertEquals(title,"제목 넣는 곳");
        System.out.println(title);
    }
    @Test
    @Ignore
    public void testParseBody() throws Exception{//해당 URL의 본문내용만 가져온다.
        SegyeParser segyeparser = new SegyeParser();
        String body = segyeparser.parse("http://www.segye.com/content/html/2016/10/07/20161007002895.html");
        String realBody = segyeparser.getBody(body);
        //assertNotNull(realBody);
        System.out.println(realBody);
    }
    @Test
    @Ignore
    public void testSplitBody() throws Exception{//. ? !를 기준으로 본문을 Split하여 처리한다.
        SegyeParser segyeparser = new SegyeParser();
        String body = segyeparser.parse("http://www.segye.com/content/html/2016/10/07/20161007002895.html");
        String realBody = segyeparser.getBody(body);
        //assertNotNull(realBody);
        System.out.println(realBody);
        List<String> splitedBody = segyeparser.splitToParagraph(realBody);
        for (String s : splitedBody) {
            System.out.println(s);
        }
    }
    @Test
    @Ignore
    public void testParagraphToWord() throws Exception{
        SegyeParser segyeparser = new SegyeParser();
        String body = segyeparser.parse("http://www.segye.com/content/html/2016/11/15/20161115003247.html");
        String realBody = segyeparser.getBody(body);
        List<String> wordList = segyeparser.extractWordList(realBody);//펭귄을 사용하여 전체 본문문장에서 단어 추출

        for (String s : wordList) {
            System.out.println(s);
        }
    }
    @Test
    @Ignore
    public void testParagraphToReporter() throws Exception{
        SegyeParser segyeparser = new SegyeParser();
        String body = segyeparser.parse("http://www.segye.com/content/html/2016/10/07/20161007002895.html");
        String reporter = segyeparser.extractReporter(body);

        System.out.println(reporter);
    }
}

