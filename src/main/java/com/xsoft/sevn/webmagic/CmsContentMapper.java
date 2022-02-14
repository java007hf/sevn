package com.xsoft.sevn.webmagic;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;

@Mapper
public interface CmsContentMapper {
//    @Select("SELECT * FROM USER WHERE NAME = #{name}")
//    CmsContentPO findByName(@Param("name") String name);

//    @Insert("INSERT INTO cms_content(contentId) VALUES(#{contentId})")
//    int insert(@Param("contentId") String contentId);

    @Insert("INSERT INTO cms_content(contentId, title, content, releaseDate) VALUES(#{contentId}, #{title}, #{content}, #{releaseDate})")
    int insert(@Param("contentId") String contentId, @Param("title") String title, @Param("content") String content, @Param("releaseDate") Date releaseDate);
}
