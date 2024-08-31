package com.yulin.es.boot.esboot.es;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "poem-index")
@Data
public class Poem {

    // 对应 document._id，为字符串
    @Id
    private String id;

    // 对应 document._source['title']
    @Field(type = FieldType.Text)
    private String title;

    // 对应 document._source['text']
    @Field(type = FieldType.Text)
    private String text;

}
