package com.day.examp3.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.day.examp3.pojo.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CommentMapper extends BaseMapper<Comment> {

    //@Select("select * from lmonkey_comment where product_id = #{pid}")
    List<Comment> getCommentsByProductId(@Param("pid")String pid);

    @Select("select COUNT(*) from lmonkey_comment where product_id = #{pid}")
    int getCommentAmountByProductId(@Param("pid")String pid);

    //@Select("select * from lmonkey_comment where user_id = #{pid}")
    List<Comment> getCommentsByUserId(@Param("uid")String uid);

    @Select("select * from lmonkey_comment")
    List<Comment> getAllComments();




}
