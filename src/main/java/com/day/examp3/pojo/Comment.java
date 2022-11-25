package com.day.examp3.pojo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("lmonkey_comment")
public class Comment {

    @TableId("comment_id")
    private String commentId;
    private String userId;
    private User user;
    private String productId;
    private Product product;
    private String context;
    private Timestamp createTime;
    private Integer likes;

    public Date getDate(){
        return new Date(this.createTime.getTime());
    }

}
