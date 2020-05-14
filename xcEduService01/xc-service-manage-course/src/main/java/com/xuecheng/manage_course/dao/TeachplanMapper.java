package com.xuecheng.manage_course.dao;

import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author zs
 * @version 1.0
 * @create 2020/5/13 15:11
 */
@Mapper
public interface TeachplanMapper {
    //课程计划查询
    public TeachplanNode selectList(String courseid);
}














