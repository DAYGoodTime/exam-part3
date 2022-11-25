package com.day.examp3.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("lmonkey_user_collection")
public class Collection {
    @TableId(value = "collection_id",type = IdType.AUTO)
    private Long collectionId;
    private String userId;
    private String productId;
    private Timestamp createTime;
}
