package com.dj.spider;

import com.dj.domain.Product;
import com.dj.dao.ProductDao;
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
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Slave {
    //private static final ArrayBlockingQueue<String> arrayBlockingQueue = new ArrayBlockingQueue<String>(1000);
    static ProductDao productDao = new ProductDao();
    private final static ExecutorService threadPool = Executors.newFixedThreadPool(10);
    public static void main(String[] args) {
        for(int i = 0;i<20;i++)
            threadPool.execute(new Runnable() {
                public void run() {
                    Jedis jedis = new Jedis("192.168.25.135",6379);
                    while (true) {
                        try {
                            List<String> list = jedis.brpop(1, "spider:jd:url");
                            parseProductDetail(list.get(1));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
    }
    private static void parseProductDetail(String id) throws IOException {
        //http请求
        String detailurl = "https://item.jd.com/" + id + ".html";
        HttpGet httpGet = new HttpGet(detailurl);
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = httpClient.execute(httpGet);
        if(200 == response.getStatusLine().getStatusCode()) {
            String s = EntityUtils.toString(response.getEntity(), Charset.forName("UTF-8"));
            Document documentdetail = Jsoup.parse(s);
            Elements title = documentdetail.select("div[class=sku-name]");
            Elements name = documentdetail.select("ul[class=parameter2 p-parameter-list] li");
            Product product = new Product();
            product.setName(name.attr("title"));
            product.setId(id);
            product.setTitle(title.text());

            Elements band = documentdetail.select(".Ptable dd");
            Elements type = documentdetail.select(".Ptable dd");
            product.setBand(band.get(0).text());
            product.setType(type.get(2).text());
            System.out.println(product);
            productDao.saveProduct(product);

        }

    }
}
