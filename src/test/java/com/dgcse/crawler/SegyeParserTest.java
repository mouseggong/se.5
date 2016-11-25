package com.dgcse.crawler;
import com.dgcse.crawler.entity.HttpResult;
import com.dgcse.crawler.module.SegyeParser;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.After;
import org.junit.Before;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

public class    SegyeParserTest {

    private static final String TEST_URL1 = "http://www.segye.com/content/html/2016/11/23/20161123003832.html"; //사진기사
    private static final String TEST_URL2 = "http://www.segye.com/content/html/2016/11/23/20161123004010.html"; //일반기사
    private static final String TEST_URL3 = "http://www.segye.com/content/html/2016/11/24/20161124003979.html"; //특이케이스 기자
    private SegyeParser parser;

    @Before
    public void setUp() throws Exception{
        parser = new SegyeParser();
        parser.parsePage(TEST_URL2);
    }

    @After
    public void tearDown(){
    }

    @Test
    @Ignore
    public void test() throws Exception {//2016년 10월 03일의 기사들의 URL주소를 가져오는 메소드
        HttpResult httpResult = parser.getDaily("2016", "10", "07", 1);//파서로 해당 날짜의 httpResult를 생성
        List<String> urlList = parser.getNewsUrlList(httpResult);//파서로 해당 날짜의 httpResult의 뉴스 URL리스트를 생성

        for (String str : urlList) {
            System.out.println(str);
        }
    }

    @Test
    @Ignore
    public void isFinalPageTest() throws Exception{
        assertTrue(parser.isFinalPage(parser.parse("http://www.segye.com/issue/leading.jsp?page=33&categoryId=0102020000000&yyyy=2016&mm=11&dd=15").getBody())); // 33page는 뉴스가 없기 때문에 True를 리턴하므로 assertTrue로 테스트
        assertFalse(parser.isFinalPage(parser.parse("http://www.segye.com/issue/leading.jsp?page=31&categoryId=0102020000000&yyyy=2016&mm=11&dd=15").getBody())); // 31page는 뉴스가 있기 때문에 False를 리턴하므로 assertFalse로 테스트
    }

    @Test
    @Ignore
    public void testParseTitle(){
        String title = parser.getTitle();
        System.out.println(title);
        //assertEquals("[S 스토리] \"직접 만든 캐릭터로 세계와 소통하고 싶어요\"",title);
    }

    @Test
    @Ignore
    public void testParseBody(){
        String body = parser.getBody();
        System.out.println(body);
        assertNotNull(body);
    }

    @Test
    @Ignore
    public void testParseDate(){
        String date = parser.getDate();
        System.out.println(date);
        assertNotNull(date);
    }

    @Test
    @Ignore
    public void testParseReporter(){
        String reporter = parser.getReporter();
        System.out.println(reporter);
        //assertNotNull(reporter);
    }

    @Test
    @Ignore
    public void testParsePhoto(){
        String p_url = parser.getPhoto_url();
        System.out.println(p_url);
        assertNotNull(p_url);
    }

    @Test
    @Ignore
    public void testChecktype(){
        System.out.println(parser.check_NewsType());
    }

    @Test
    @Ignore
    public void testNeospageloop() throws Exception {//해당 날짜의 모든 page에 해당하는 뉴스의 URL리스트를 뽑아내는 test
        HttpResult[] httpResult = new HttpResult[50]; //넉넉하게 사이즈 잡음 (보통 최소 15page ~ 최대 31~32page까지 있음)
        List<String>[] urlList = new List[50];
        for(int index = 0; index < httpResult.length; index++){
            httpResult[index] = parser.getDaily("2016", "11", "15", index+1);
            if(parser.isFinalPage(httpResult[index].getBody()))//마지막 페이지 다음 페이지이면 break
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
    @Ignore
    public void test_WeekNewsURL() throws Exception{//7일간의 전체 뉴스를 추출하는 메소드
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
    public void testSplitBody() throws Exception{//. ? !를 기준으로 본문을 Split하여 처리한다.
        String realBody = parser.getBody();
        assertNotNull(realBody);
        System.out.println(realBody);
        List<String> splitedBody = parser.splitToParagraph(realBody);
        for (String s : splitedBody) {
            System.out.println(s);
        }
    }

    @Test
    @Ignore
    public void testParagraphToWord() throws Exception{
        String realBody = parser.getBody();
        List<String> wordList = parser.extractWordList(realBody);//펭귄을 사용하여 전체 본문문장에서 단어 추출

        for (String s : wordList) {
            System.out.println(s);
        }

        System.out.println(wordList.size()); // 추출된 단어의 개수
    }

    @Test
    //@Ignore
    public void testcountWordinNews() throws Exception{
        HashMap<String, Integer> test_hash = parser.countWordinNews();
        Set<String> key_set = test_hash.keySet();
        for(String word : key_set){
            System.out.println(word + " : " + test_hash.get(word));
        }
    }
}