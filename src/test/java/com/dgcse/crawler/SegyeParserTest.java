package com.dgcse.crawler;
import com.dgcse.crawler.entity.HttpResult;
import org.junit.Ignore;
import org.junit.Test;
import java.util.List;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


/**
 * Created by LeeHyungRae on 2016. 10. 10..
 */
public class SegyeParserTest {

    @Test
    @Ignore
    public void test() throws Exception{
        SegyeParser parser = new SegyeParser();
        HttpResult httpResult = parser.getDaily("2016","10","03",1);

        List<String> urlList = parser.getNewsUrlList(httpResult);
        //for(String str : urlList){
        System.out.println(httpResult);
        //}
        for(String str : urlList){
            //System.out.println(str);
        }
    }
    @Test
    @Ignore
    public void testCrawler() throws Exception{
        SegyeParser segyeparser = new SegyeParser();
        String result = segyeparser.parse("http://www.segye.com/content/html/2016/10/07/20161007002895.html");
        //assertNotNull(result);
        System.out.println(result);
    }
    @Test
    @Ignore
    public void testParseTitle() throws Exception{
        SegyeParser segyeparser = new SegyeParser();
        String body = segyeparser.parse("http://www.segye.com/content/html/2016/10/07/20161007002895.html");
        String title = segyeparser.getTitle(body);
        //assertEquals(title,"제목 넣는 곳");
        System.out.println(title);
    }
    @Test
    @Ignore
    public void testParseBody() throws Exception{
        SegyeParser segyeparser = new SegyeParser();
        String body = segyeparser.parse("http://www.segye.com/content/html/2016/10/07/20161007002895.html");
        String realBody = segyeparser.getBody(body);
        //assertNotNull(realBody);
        System.out.println(realBody);
    }
    @Test
    //@Ignore
    public void testSplitBody() throws Exception{
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
        String body = segyeparser.parse("http://www.segye.com/content/html/2016/10/07/20161007002895.html");
        String realBody = segyeparser.getBody(body);
        List<String> wordList = segyeparser.extractWordList(realBody);

        for (String s : wordList) {
            System.out.println(s);
        }
    }
}

