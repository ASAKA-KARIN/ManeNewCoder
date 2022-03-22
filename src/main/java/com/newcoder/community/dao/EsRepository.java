package com.newcoder.community.dao;

import com.newcoder.community.pojo.DiscussPost;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EsRepository extends ElasticsearchRepository<DiscussPost,Integer> {

}
