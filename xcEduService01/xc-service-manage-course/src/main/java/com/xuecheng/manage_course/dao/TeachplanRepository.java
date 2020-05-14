package com.xuecheng.manage_course.dao;

import com.xuecheng.framework.domain.course.Teachplan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author zs
 * @version 1.0
 * @create 2020/5/13 17:03
 */

public interface TeachplanRepository extends JpaRepository<Teachplan,String> {
    //查询Teachplan,根据courseId和parentId
    public List<Teachplan> findByCourseidAndParentid(String courseId,String parentId);
}












