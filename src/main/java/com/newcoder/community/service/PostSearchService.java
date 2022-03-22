package com.newcoder.community.service;

import com.newcoder.community.dao.EsRepository;
import com.newcoder.community.pojo.DiscussPost;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 86156
 * 完成帖子的查询功能
 */
@Service
public class PostSearchService {

    @Autowired
    EsRepository esRepository;

    @Qualifier("esRestTemplate")
    @Autowired
    ElasticsearchRestTemplate restTemplate;

    /**
     * 当有新帖时，存入一份至es
     * @param post
     */
    public void addPost(DiscussPost post)
    {
        esRepository.save(post);
    }
    /**
     * 删除帖子时，删除es中的数据
     * @param post
     */
    public void delPost(DiscussPost post)
    {
        esRepository.delete(post);
    }
    public void updatePost(DiscussPost post)
    {
        esRepository.save(post);
    }
    public List<DiscussPost> searchPost(String keyWord)
    {
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.multiMatchQuery(keyWord,"title","content"))
                .withSorts(SortBuilders.fieldSort("type").order(SortOrder.DESC)
                        ,SortBuilders.fieldSort("score").order(SortOrder.DESC)
                        ,SortBuilders.fieldSort("createTime").order(SortOrder.DESC))
                .withHighlightFields(new HighlightBuilder.Field("title").preTags("<em>").postTags("</em>"),
                        new HighlightBuilder.Field("content").preTags("<em>").postTags("</em>"))
                .build();
        SearchHits<DiscussPost> searchHits = restTemplate.search(searchQuery, DiscussPost.class);
        List<DiscussPost> discussPosts = new ArrayList<>();
        for(SearchHit searchHit:searchHits)
        {
           DiscussPost post = (DiscussPost)searchHit.getContent();
           StringBuilder titles = new StringBuilder();
            List<String> title = searchHit.getHighlightField("title");
            for (String s:title)
            {
                titles.append(s);
            }
            post.setTitle(titles.toString());
            List<String> content = searchHit.getHighlightField("content");
            StringBuilder contents = new StringBuilder();
            for(String s:content)
            {
                contents.append(s);
            }
            post.setContent(contents.toString());
            discussPosts.add(post);
        }
        return discussPosts;
    }
}
