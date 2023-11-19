package com.yupi.yuso;

import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.yupi.yuso.model.entity.Post;
import com.yupi.yuso.service.PostService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Fickler
 * @date 2023/11/17 22:45
 */

public class CrawlerTests {

    @Resource
    private PostService postService;

    @Test
    void testFetchPicture() throws IOException {
        int current = 1;
        String url = "https://cn.bing.com/images/search?q=小黑子&first=" + current;
        Document doc = Jsoup.connect(url).get();
        System.out.println(doc);
        Elements elements = doc.select(".iuscp.isv");
        for (Element element : elements) {
            // 取图片地址 (murl)
            String m = element.select(".iusc").get(0).attr("m");
            Map<String, Object> map = JSONUtil.toBean(m, Map.class);
            String murl = (String) map.get("murl");
            System.out.println(murl);
            // 取标题
            String title = element.select(".inflnk").get(0).attr("aria-label");
            System.out.println(title);
        }
    }

    // 1. 获取数据
    @Test
    void testFetchPassage() {
        String json = "{\"sortField\":\"createTime\",\"sortOrder\":\"descend\",\"reviewStatus\":1,\"current\":1}";
        String result2 = HttpRequest.post("https://www.code-nav.cn/api/post/list/page/vo")
                .body(json)
                .execute().body();
//        System.out.println(result2);
        // 2. json 转数据类型
        Map<String, Object> map = JSONUtil.toBean(result2, Map.class);
        JSONObject data = (JSONObject) map.get("data");
        JSONArray records = (JSONArray) data.get("records");
        List<Post> postList = new ArrayList<>();
        for (Object record : records) {
            JSONObject tempRecord = (JSONObject) record;
            Post post = new Post();
            // todo 数据校验是否为空
            post.setTitle(tempRecord.getStr("title"));
            post.setContent(tempRecord.getStr("content"));
            JSONArray tags = (JSONArray) tempRecord.get("tags");
            List<String> tagList = tags.toList(String.class);
            post.setTags(JSONUtil.toJsonStr(tagList));
            post.setUserId(1L);
            postList.add(post);
        }
//        System.out.println(postList);
        // 3. 数据入库
        boolean b = postService.saveBatch(postList);
        Assertions.assertTrue(b);
    }

}
