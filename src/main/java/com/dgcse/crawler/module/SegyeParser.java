package com.dgcse.crawler.module;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

import com.dgcse.crawler.entity.HttpResult;
import com.google.common.collect.Interner;
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
 * Created by Ianohjh on 2016-11-12.
 * 각 HTML 태그의 class id 정리 (MACRO)
 * 세계일보의 전체뉴스를 볼수 있는 List URL = BASE_LIST_URL
 */

public class SegyeParser extends BaseParser{

    private static final String NEWS_LIST_ID = "news_list";
    private static final String TITLE_CLASS = "titleh1";
    private static final String BODY_ID = "article_txt";
    private static final String END_OF_PAGE = "no_articles";
    private static final String DATE_OF_NEWS = "SG_ArticleDateLine";
    private static final String NEWS_TYPE = "articleView-Box";
    private static final String REPORTER_ID = "SG_CreatorName";

    private static final String BASE_LIST_URL = "http://www.segye.com/issue/leading.jsp?categoryId=0102020000000";
    private Document doc;

    public SegyeParser(){

    }

    //년,월,일,페이지를 매개변수로 전체 url을 반환하는 함수
    public String getListUrlByDate(String year,String month,String date,int page){
        return BASE_LIST_URL+"&page="+page+"&yyyy="+year+"&mm="+month+"&dd="+date;
    }

    //전체 page에 대한 1일치의 모든 기사 URL List를 추출하는 메소드
    public List<String> getNewsUrlListByDate(String year,String month,String date){
        List<String> urlList = new ArrayList<String>();
        int startPos = 1;
        boolean isPageExist = true;
        while(isPageExist){//페이지가 있을때까지 반복
            try {
                HttpResult httpResult = parse(getListUrlByDate(year, month, date, startPos++));
                if(isFinalPage(httpResult.getBody())) {//마지막 페이지인지 판별
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

    //해당 HttpResult객체를 매개변수로 받아 1개의 page에서의 뉴스 기사 URL List를 추출하는 메소드 (일반적으로 1page 당 20개)
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

    //특정 날짜(년,월,일,page)에 대한 HttpResult객체를 만들어 반환한다.
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

    //1주일치의 모든 기사(1일 기준 20~35page / 1page기준 20개 뉴스기사)의 URL List를 추출하여 반환한다.
    public List<String>[] oneWeek_urlList(){ //(현재날짜 - 1일)부터 1주일 전까지의 총 7일간의 전체 뉴스 추출
        HttpResult[][] httpResult = new HttpResult[7][40]; //넉넉하게 사이즈 잡음 (보통 최소 15page ~ 최대 31~32page까지 있음)
        List<String>[] urlList = new List[280];
        int URL_index = 0;
        int index = 0;
        long date_time = 0;
        long one_day = 24*60*60*1000; // 1일치 계산값
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
                        if (this.isFinalPage(httpResult[index_day][index].getBody()))//마지막 페이지의 다음 페이지(없는 페이지)이면 break
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

    //해당 페이지가 마지막 페이지(유효)의 다음 페이지(무효)인지 아닌지를 판별하는 메소드
    public boolean isFinalPage(String html){ // 해당 날짜의 뉴스의 page가 끝이면 true 아니면 false 리턴
        Document doc = Jsoup.parse(html);

        Elements elements = doc.getElementsByClass(END_OF_PAGE);
        return elements.size()==1;
    }

    @Override
    public void parsePage(String url) throws Exception{//해당 URL이 파싱이 되는지 확인하는 메소드
        HttpResult httpResult = parse(url);
        if(httpResult.getCode()%2!=0)
            throw new Exception("페이지가 정상적으로 파싱되지 않았습니다.\nURL : "+url+", Status Code : "+httpResult.getCode());
        doc = Jsoup.parse(httpResult.getBody());
        if(doc==null)
            throw new Exception("Document가 정상적으로 파싱되지 않았습니다.\nURL : "+url);
    }

    @Override
    public String getTitle() {//기사의 제목 추출
        try{
            return doc.getElementsByClass(TITLE_CLASS).text();
        }
        catch(Exception e){
            return "getTitle Error";
        }
    }

    @Override
    public String getBody() {//기사의 본문 추출
        try{
            String realbody = doc.getElementById(BODY_ID).text();
            if(realbody.contains("\uD83D\uDC6B")) {//특수 이모티콘 처리 (InteliJ에서는 처리가능 But, DB에서 처리 불가능)
                realbody = realbody.replace("👫", "");
            }
            return realbody;
        }
        catch(Exception e){
            return "getBody Error";
        }
    }

    @Override
    public String getReporter() { //기자이름 추출 (수정 완료)
        String realBody = this.getBody();
        if(realBody.length() < 2)// 본문이 없는 경우
            return "NONE";
        String[] split_reporter = new String[2];
        String E_reporter = doc.getElementById(REPORTER_ID).text(); //HTML파일 내부 기자이름 class id 사용
        try{
            if(E_reporter.contains("기자")){  // 추출된 최종 변수에 기자라는 표현이 붙어있으면 제거
                split_reporter = E_reporter.split(" ");
                return split_reporter[0];
            }
            else if(E_reporter.contains("2")){  //추출된 최종 변수에 숫자 2가 붙어있으면 제거
                return E_reporter.replace("2","");
            }
            else if (E_reporter == null){
                return "NONE";
            }
            else if (E_reporter.matches(".*[a-z]")){ // 추출된 최종 변수에 이메일ID를 사용한 사용자의 이름 따로 추출 (기자이름에 이름이 아닌 메일ID가 있는 경우)
                String[] split_word = new String[1000];
                split_word = realBody.split(" ");
                for(int find = split_word.length - 1; find > 0; find--){
                    if(split_word[find].contains(E_reporter)) {
                        E_reporter = split_word[find - 2];
                        break;
                    }
                }
                if(E_reporter.contains("=")) {
                    split_reporter = E_reporter.split("=");
                    E_reporter = split_reporter[1];
                }
                else if(E_reporter.contains("·")) {
                    split_reporter = E_reporter.split("·");
                    E_reporter = split_reporter[0];
                }
                else
                    return E_reporter;
            }
            else
                return E_reporter;
        }
        catch (Exception e){
            return "getReporter Error";
        }
        return E_reporter;
    }

    @Override
    public String getDate() { //기사의 날짜 추출 yyyymmdd 형식으로 추출
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

    @Override
    public String getPhoto_url() { // 기사에 만약 대표사진이 있을 경우 세계일보 서버에 저장된 대표사진URL추출
        String tag_Photo = doc.getElementById("article_txt").select("img[src$=.jpg").toString();//select에서 정규표현식 사용
        String[] split_word = new String[50];
        split_word = tag_Photo.split("\"");
        try{
            return split_word[split_word.length-2];
        }
        catch(Exception e){
            return "getPhoto_url Error";
        }
    }

    @Override
    public boolean check_NewsType(){//해당 뉴스기사가 일반 기사인지 포토NEWS인지를 판별하는 메소드
        if(doc.getElementById(NEWS_TYPE) == null) // 사진 기사의 ID가 존재하지 않는다 = 일반기사
            return true;
        else // 사진기사의 ID가 존재 = 사진기사
            return false;
    }

    public List<String> splitToParagraph(String body){//추출된 기사의 본문을 문장 기준으로 분할 해준다.
        String[] splitedLine = body.split("\\.|\\?|!");
        List<String> splitedList = Lists.newArrayList(splitedLine);
        List<String> result_List = Lists.newArrayList();
        for (String s : splitedList) {
            if(s.length()>3)
                result_List.add(s);
        }
        return result_List; // 최종 문장으로 판별된 String만 List화 하여 반환
    }

    /**
     * 펭귄 API 사용 하였음!!! - 한글 형태소 분석기 중 명사(noun)를 추출하는 부분을 확인하여 사용
     * stemmingLine() -> extractWordList()를 통해서 기사 본문의 명사만을 추출할 수 있다.
     */
    //펭귄API를 사용하여 형태소 분석을 한 List를 반환한다.
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

    //stemmingLine()의 반환 List에서 명사만을 추출하여 List에 저장후 반환하는 메소드
    public List<String> extractWordList(String body){
        List<KoreanTokenJava> list = stemmingLine(body);
        List<String> wordList = Lists.newArrayList();

        if(list.size()==0)
            return Lists.newArrayList();

        //실제 사용할 단어(키워드로 사용할)만 걸러낸다.
        for (KoreanTokenJava koreanTokenJava : list) {
            //고유명사와 명사만을 걸러낸다.
            if ((koreanTokenJava.getPos().equals(KoreanPosJava.ProperNoun) || koreanTokenJava.getPos().equals(KoreanPosJava.Noun)) && koreanTokenJava.getLength() > 1){
                String word = koreanTokenJava.getText();
                wordList.add(word);
            }
        }
        return wordList;
    }

    //1개 기사의 각 단어가 기사에 나온 횟수를 count하여 HashMap으로 저장하여 반환하는 메소드
    public HashMap<String,Integer> countWordinNews(){
        HashMap<String,Integer> H_word_count = new HashMap<String,Integer>();//key:단어 / Value:해당 단어가 해당기사에 나온 count수
        List<String> wordList = this.extractWordList(this.getBody());
        HashSet<String> Check_duplicate = new HashSet<String>();

        for(String word : wordList){
            if(!Check_duplicate.contains(word)){//초기 단어가 set에 없을때 (1로 세팅)
                H_word_count.put(word,1);
                Check_duplicate.add(word);
            }
            else{//set에 이미 단어가 있을때 count증가
                H_word_count.put(word,H_word_count.get(word)+1);
            }
        }
        return H_word_count;
    }
}