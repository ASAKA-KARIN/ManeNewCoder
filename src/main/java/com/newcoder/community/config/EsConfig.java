package com.newcoder.community.config;

import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;
import org.springframework.data.elasticsearch.config.AbstractElasticsearchConfiguration;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;

/**
 * @author Yoshino
 * elasticsearch配置类
 */
@Configuration
public class EsConfig extends AbstractElasticsearchConfiguration {

    @Override
    @Bean
    public RestHighLevelClient elasticsearchClient() {
        ClientConfiguration clientConfiguration = ClientConfiguration.builder()
                .connectedTo("101.42.117.137:9200")
                .build();
        RestHighLevelClient client = RestClients.create(clientConfiguration).rest();
        return client;
    }
    @Bean("esRestTemplate")
    public ElasticsearchRestTemplate esRestTemplate() {

        return new ElasticsearchRestTemplate(elasticsearchClient());
    }
}
