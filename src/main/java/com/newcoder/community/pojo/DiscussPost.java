package com.newcoder.community.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

/**
 * @author Yoshino
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
@Document(indexName = "post")
public class DiscussPost implements Serializable {
    @Id
    private int id;
    @Field(type = FieldType.Integer)
    private int userId;
    @Field(type = FieldType.Text)
    private String title;
    @Field(type = FieldType.Text)
    private String content;
    /**
     * * 0-普通
     * * 1-置顶
     */
    @Field(type = FieldType.Integer)
    private int type;
    /**
     * * 0-正常
     * * 1-精华
     * * 2-拉黑
     */
    @Field(type = FieldType.Integer)
    private int status;
    @Field(type = FieldType.Date)
    private Date createTime;
    @Field(type = FieldType.Integer)
    private int commentCount;
    @Field(type = FieldType.Double)
    private double score;
}
