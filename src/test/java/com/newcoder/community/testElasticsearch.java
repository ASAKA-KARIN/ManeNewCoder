package com.newcoder.community;

import com.newcoder.community.dao.DiscussPostMapper;
import com.newcoder.community.dao.EsRepository;
import com.newcoder.community.pojo.DiscussPost;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;
import java.util.Map;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class testElasticsearch {
    @Autowired
    ElasticsearchRestTemplate esRestTemplate;
    @Autowired
    EsRepository esRepository;
    @Autowired
    DiscussPostMapper postMapper;

    @Test
    public void addInfo() {
        esRepository.save(postMapper.getPost(239));
        esRepository.save(postMapper.getPost(240));
        esRepository.save(postMapper.getPost(241));
        esRepository.save(postMapper.getPost(242));
    }

    @Test
    public void addAllInfo() {
        esRepository.saveAll(postMapper.getPosts(103,0));
    }

    @Test
    public void testDel() {
        esRepository.deleteAll();
    }

    @Test
    public void testQuery() {
        String text = "互联网求职";
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.multiMatchQuery(text, "title", "content"))
                .withSorts(SortBuilders.fieldSort("type").order(SortOrder.DESC)
                        , SortBuilders.fieldSort("score").order(SortOrder.DESC)
                        , SortBuilders.fieldSort("createTime").order(SortOrder.DESC))
                .withPageable(PageRequest.of(1, 8))
                .withHighlightFields(new HighlightBuilder.Field("title").preTags("<em>").postTags("</em>"),
                        new HighlightBuilder.Field("content").preTags("<em>").postTags("</em>"))
                .build();
        SearchHits<DiscussPost> searchHits = esRestTemplate.search(searchQuery, DiscussPost.class);

        for (SearchHit pos : searchHits) {
            Map<String, List<String>> fields = pos.getHighlightFields();
            for (Map.Entry<String, List<String>> entry : fields.entrySet()) {
                System.out.println(entry.getKey());
                System.out.println(entry.getValue());
            }
        }
    }


}
