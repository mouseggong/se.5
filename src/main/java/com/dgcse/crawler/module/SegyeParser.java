package com.dgcse.crawler.module;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;

import com.dgcse.crawler.entity.HttpResult;
import com.google.common.collect.Lists;
import com.twitter.penguin.korean.KoreanPosJava;
import com.twitter.penguin.korean.KoreanTokenJava;
import com.twitter.penguin.korean.TwitterKoreanProcessorJava;
import com.twitter.penguin.korean.tokenizer.KoreanTokenizer;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import scala.collection.Seq;

public class SegyeParser extends BaseParser{

    private static final String NEWS_LIST_ID = "news_list";
    private static final String TITLE_CLASS = "titleh1";
    private static final String BODY_ID = "article_txt";
    private static final String END_OF_PAGE = "no_articles";
    private static final String DATE_OF_NEWS = "SG_ArticleDateLine";

    private static final String BASE_LIST_URL = "http://www.segye.com/issue/leading.jsp?categoryId=0102020000000";
    private Document doc;

    public SegyeParser(){
    }
    private String getListUrlByDate(String year,String month,String date,int page){
        return BASE_LIST_URL+"&page="+page+"&yyyy="+year+"&mm="+month+"&dd="+date;
    }

    public List<String> getNewsUrlListByDate(String year,String month,String date){
        List<String> urlList = new ArrayList<String>();
        int startPos = 1;
        boolean isPageExist = true;
        while(isPageExist){
            try {
                HttpResult httpResult = parse(getListUrlByDate(year, month, date, startPos++));
                if(isFinalPage(httpResult.getBody())) {
                    isPageExist = false;
                    break;
                }
                urlList.addAll(getNewsUrlList(httpResult));
            }
            catch(Exception e){
                isPageExist = false;
            }
        }
        return urlList;
    }

    public List<String> getNewsUrlList(HttpResult httpResult) throws Exception{
        Document doc = Jsoup.parse(httpResult.getBody());

        Element listElement = doc.getElementById(NEWS_LIST_ID);
        Element innerElement = listElement.getElementsByClass("bd").get(0);
        Elements elements = innerElement.getElementsByClass("title_cr");

        List<String> urlList = new ArrayList<String>();

        for(int i = 0;i<elements.size();i++)
            urlList.add(elements.get(i).attr("href"));

        return urlList;
    }

    public HttpResult getDaily(String year, String month, String date, int page) throws IOException {
        String url = BASE_LIST_URL+"&page="+page+"&yyyy="+year+"&mm="+month+"&dd="+date;

        Request request = new Request.Builder().url(url).get().build();
        OkHttpClient okHttpClient = new OkHttpClient();
        Response response = okHttpClient.newCall(request).execute();

        int code = response.code();
        String body = response.body().string();

        response.close();

        return new HttpResult(code,body);
    }

    public List<String>[] oneWeek_urlList(){ //현재날짜 - 1일부터 1주일 전까지의 총 7일간의 전체 뉴스 추출
        HttpResult[][] httpResult = new HttpResult[7][40]; //넉넉하게 사이즈 잡음 (보통 최소 15page ~ 최대 31~32page까지 있음)
        List<String>[] urlList = new List[280];
        int URL_index = 0;
        int index = 0;
        long date_time = 0;
        long one_day = 24*60*60*1000;
        HttpResult temp;
        Date ch_date;
        DateFormat df = new SimpleDateFormat("yyyyMMdd");
        Calendar today = Calendar.getInstance();
        String all_date = Integer.toString(today.get(Calendar.YEAR)) + Integer.toString(today.get(Calendar.MONTH)+1) + Integer.toString(today.get(Calendar.DAY_OF_MONTH)-1);
        try{
            for(int index_day = 0; index_day < 7; index_day++) {
                if(all_date != ""){
                    for (index = 0; index < httpResult[index_day].length; index++) {
                        httpResult[index_day][index] = this.getDaily(all_date.substring(0, 4), all_date.substring(4, 6), all_date.substring(6, 8), index + 1);
                        if (this.isFinalPage(httpResult[index_day][index].getBody()))//마지막 페이지의 다음 페이지이면 break
                            break;
                        temp = httpResult[index_day][index];
                        urlList[URL_index + index] = this.getNewsUrlList(temp);
                    }
                    URL_index += index;//해당 인덱스일때는 null인 값이니깐, index+1이 아니라 index를 더한다.
                    date_time = df.parse(all_date).getTime();
                    date_time -= one_day;//해당 날짜에서 1일을 빼는 과정

                    ch_date = new Date(date_time);
                    all_date = df.format(ch_date);//다시 날짜형식으로 파싱
                }
            }
        } catch (ParseException e){
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }
        return urlList;
    }
    public boolean isFinalPage(String html){ // 해당 날짜의 뉴스의 page가 끝이면 true 아니면 false 리턴
        Document doc = Jsoup.parse(html);

        Elements elements = doc.getElementsByClass(END_OF_PAGE);
        return elements.size()==1;
    }

    @Override
    public void parsePage(String url) throws Exception{
        HttpResult httpResult = parse(url);
        if(httpResult.getCode()%2!=0)
            throw new Exception("페이지가 정상적으로 파싱되지 않았습니다.\nURL : "+url+", Status Code : "+httpResult.getCode());
        doc = Jsoup.parse(httpResult.getBody());
        if(doc==null)
            throw new Exception("Document가 정상적으로 파싱되지 않았습니다.\nURL : "+url);
    }

    @Override
    public String getTitle() {
        try{
            return doc.getElementsByClass(TITLE_CLASS).text();
        }
        catch(Exception e){
            return "getTitle Error";
        }
    }

    @Override
    public String getBody() {
        try{
            return doc.getElementById(BODY_ID).text();
        }
        catch(Exception e){
            return "getBody Error";
        }
    }

    @Override
    public String getReporter() {
        String realBody = this.getBody();
        List<String> wordList = this.extractWordList(realBody);
        try{
            return wordList.get(wordList.size()-2);
        }
        catch(Exception e) {
            return "getReporter Error";
        }
    }

    @Override
    public String getDate() {
        String original_Date = doc.getElementById(DATE_OF_NEWS).text();
        String[] split_date = new String[10];
        split_date = original_Date.split(" ");
        String[] date = new String[3];
        date = split_date[1].split("-");
        try{
            return date[0]+date[1]+date[2];
        }
        catch(Exception e){
            return "getDate Error";
        }
    }

    public List<String> splitToParagraph(String body){
        String[] splitedLine = body.split("\\.|\\?|!");
        List<String> splitedList = Lists.newArrayList(splitedLine);
        List<String> removedList = Lists.newArrayList();
        for (String s : splitedList) {
            if(s.length()>3)
                removedList.add(s);
        }
        return removedList; // 우리가 생각하기에는 갖고 올 문장들을 갖고있는 배열을 리턴해준다.
    }

    public static List<KoreanTokenJava> stemmingLine(String line){
        try {
            CharSequence normalized = TwitterKoreanProcessorJava.normalize(line);
            Seq<KoreanTokenizer.KoreanToken> tokens = TwitterKoreanProcessorJava.tokenize(normalized);
            Seq<KoreanTokenizer.KoreanToken> stemmed = TwitterKoreanProcessorJava.stem(tokens);
            return TwitterKoreanProcessorJava.tokensToJavaKoreanTokenList(stemmed);
        }
        catch(Exception e){
            return Lists.newArrayList();
        }
    }

    public List<String> extractWordList(String body){
        List<KoreanTokenJava> list = stemmingLine(body);
        List<String> wordList = Lists.newArrayList();

        if(list.size()==0)
            return Lists.newArrayList();

        //실제 사용할 단어만 걸러낸다.
        for (KoreanTokenJava koreanTokenJava : list) {
            //고유명사와 명사만을 걸러낸다.
            if ((koreanTokenJava.getPos().equals(KoreanPosJava.ProperNoun) || koreanTokenJava.getPos().equals(KoreanPosJava.Noun)) && koreanTokenJava.getLength() > 1){
                String word = koreanTokenJava.getText();
                wordList.add(word);
            }
        }
        return wordList;
    }
}