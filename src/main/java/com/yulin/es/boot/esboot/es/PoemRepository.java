package com.yulin.es.boot.esboot.es;

import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface PoemRepository extends ElasticsearchRepository<Poem, String> {
    // 根据text字段模糊匹配
    @Query("{\"match\": {\"text\": {\"query\": \"?0\"}}}")
    List<Poem> findByText(String text);

    // 根据text和title字段模糊匹配,fuzziness使用"AUTO" 使查询支持模糊匹配，根据查询词的长度自动决定模糊度
    @Query("{\"multi_match\": {\"query\": \"?0\", \"fields\": [\"text\", \"title\"], \"fuzziness\": \"AUTO\"}}")
    List<Poem> findByTextOrTitle(String text);
}
