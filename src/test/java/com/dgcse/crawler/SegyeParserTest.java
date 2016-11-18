package com.dgcse.crawler;
import com.dgcse.crawler.entity.HttpResult;
import org.junit.Ignore;
import org.junit.Test;

import javax.validation.constraints.Null;
import java.util.List;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.Assert.*;

public class SegyeParserTest {

    @Test
    @Ignore
    public void test() throws Exception {//2016년 10월 03일의 기사들의 URL주소를 가져오는 메소드
        SegyeParser parser = new SegyeParser();
        HttpResult httpResult = parser.getDaily("2016", "10", "03", 1);//파서로 해당 날짜의 httpResult를 생성

        List<String> urlList = parser.getNewsUrlList(httpResult);//파서로 해당 날짜의 httpResult의 뉴스 URL리스트를 생성

        for (String str : urlList) {
            System.out.println(str);
        }
    }

    @Test
    @Ignore
    public void testNewspageloop() throws Exception {//해당 날짜의 모든 page에 해당하는 뉴스의 URL리스트를 뽑아내는 test
        SegyeParser parser = new SegyeParser();
        HttpResult[] httpResult = new HttpResult[50]; //넉넉하게 사이즈 잡음 (보통 최소 15page ~ 최대 31~32page까지 있음)
        List<String>[] urlList = new List[50];
        for(int index = 0; index < httpResult.length; index++){
            httpResult[index] = parser.getDaily("2016", "11", "15", index+1);
            if(parser.isFinalPage(httpResult[index]))//마지막 페이지 다음 페이지이면 break
                break;
            urlList[index] = parser.getNewsUrlList(httpResult[index]);
        }
        int index = 0;
        while(urlList[index] != null){
            for(String str : urlList[index]){
                System.out.println(str);
            }
            index++;
        }
    }

    @Test
    //@Ignore
    public void test_WeekNewsURL() throws Exception{//7일간의 전체 뉴스를 추출하는 메소드
        SegyeParser parser = new SegyeParser();
        List<String>[] urlList = parser.oneWeek_urlList();
        int index = 0;
        int count = 0;
        while (urlList[index] != null) {
            for (String str : urlList[index]) {
                System.out.println(str);
                count++;
            }
            index++;
        }
        System.out.println("총 추출된 URL의 개수는: " + count);
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
        assertNotNull(realBody);
        System.out.println(realBody);
        List<String> splitedBody = segyeparser.splitToParagraph(realBody);
        for (String s : splitedBody) {
            System.out.println(s);
        }
    }

    @Test
    //@Ignore
    public void isFinalPageTest() throws Exception{
        SegyeParser parser = new SegyeParser();
        String body = parser.parse("http://www.segye.com/issue/leading.jsp?page=33&categoryId=0102020000000&yyyy=2016&mm=11&dd=15");
        assertTrue(parser.isFinalPage(new HttpResult(100,body))); // 33page는 뉴스가 없기 때문에 True를 리턴하므로 assertTrue로 테스트
        body = parser.parse("http://www.segye.com/issue/leading.jsp?page=31&categoryId=0102020000000&yyyy=2016&mm=11&dd=15");
        assertFalse(parser.isFinalPage(new HttpResult(100,body))); // 31page는 뉴스가 있기 때문에 False를 리턴하므로 assertFalse로 테스트
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