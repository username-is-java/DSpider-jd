package com.dj.spider;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Master {
    //private static final ArrayBlockingQueue<String> arrayBlockingQueue = new ArrayBlockingQueue<String>(1000);
    private final static ExecutorService threadPool = Executors.newFixedThreadPool(10);
    private final static Jedis jedis = new Jedis("192.168.25.135",6379);
    public static void main(String[] args) {

        try {
            //解析首页
            htmlParse();
            //解析分页
            doingpage();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private static void doingpage() throws Exception {
        for(int n = 1;n < 10;n++) {
            String url = "https://search.jd.com/Search?keyword=%E6%89%8B%E6%9C%BA&enc=utf-8&qrst=1&rt=1&stop=1&vt=2&wq=%E6%89%8B%E6%9C%BA&cid2=653&cid3=655&page=" + n + "&scrolling=y";
            Document doc = parseHtml(url);
            getSearchInfo(doc);
        }
    }
    private static void getSearchInfo(Document doc) throws Exception {
        Elements elements = doc.select("#J_goodsList li[data-pid]");
        for (Element element : elements) {
            String id = element.attr("data-pid");
            //System.out.println(id);
            jedis.lpush("spider:jd:url",id);
        }
    }
    private static void htmlParse() throws Exception {
        String url = "https://search.jd.com/Search?keyword=%E6%89%8B%E6%9C%BA&enc=utf-8&pvid=aa914cd360f44d989a1a4d45ecdb9a65";
        Document document = parseHtml(url);
        getSearchInfo(document);
    }

    private static Document parseHtml(String url) throws Exception {
        //http请求
        HttpGet httpGet = new HttpGet(url);
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = httpClient.execute(httpGet);
        if(200 == response.getStatusLine().getStatusCode()) {
            String s = EntityUtils.toString(response.getEntity(), Charset.forName("UTF-8"));
            return Jsoup.parse(s);
        }
        return null;
    }
}



