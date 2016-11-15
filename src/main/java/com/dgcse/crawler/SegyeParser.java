package com.dgcse.crawler;

import java.io.IOException;
import java.util.List;


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

/**
 * Created by LeeHyungRae on 2016. 10. 10..
 *
 * 세계일보 파싱 모듈
 */
public class SegyeParser {

    private static final String NEWS_LIST_ID = "news_list";
    private static final String BASE_URL = "http://www.segye.com/issue/leading.jsp?categoryId=0102020000000";

    public HttpResult getDaily(String year, String month, String date, int page) throws IOException {
        String url = BASE_URL+"&page="+page+"&yyyy="+year+"&mm="+month+"&dd="+date;

        Request request = new Request.Builder().url(url).get().build();
        OkHttpClient okHttpClient = new OkHttpClient();
        Response response = okHttpClient.newCall(request).execute();

        int code = response.code();
        String body = response.body().string();

        response.close();

        return new HttpResult(code,body);
    }

    public boolean isFinalPage(HttpResult httpResult){
        Document doc = Jsoup.parse(httpResult.getBody());

        Elements elements = doc.getElementsByClass("no_articles");
        return elements.size()==1;
    }

    public List<String> getNewsUrlList(HttpResult httpResult) throws Exception{
        Document doc = Jsoup.parse(httpResult.getBody());


        Element listElement = doc.getElementById(NEWS_LIST_ID);
        Element innerElement = listElement.getElementsByClass("bd").get(0);
        Elements elements = innerElement.getElementsByClass("title_cr");

        List<String> urlList = Lists.newArrayList();

        for(int i = 0;i<elements.size();i++)
            urlList.add(elements.get(i).attr("href"));

        return urlList;

    }
    public static String parse(String url) throws Exception{
        OkHttpClient okHttpClient = new OkHttpClient.Builder().followRedirects(false).build();
        Request request = new Request.Builder().get().url(url).build();
        Response response = okHttpClient.newCall(request).execute();
        return response.body().string();
    }
    public String getTitle(String body){
        return Jsoup.parse(body).getElementsByClass("titleh1").text();
    }

    public String getBody(String body){
        return Jsoup.parse(body).getElementById("article_txt").text();
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
