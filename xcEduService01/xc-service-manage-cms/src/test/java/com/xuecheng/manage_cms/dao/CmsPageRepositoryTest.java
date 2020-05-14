package com.xuecheng.manage_cms.dao;

import com.xuecheng.framework.domain.cms.CmsPage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Optional;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class CmsPageRepositoryTest {

    @Autowired
    private CmsPageRepository cmsPageRepository;

    @Test
    public void testFindAll(){

        List<CmsPage> all = cmsPageRepository.findAll();
        System.out.println(all);
    }

    @Test
    public void testFindPage(){
        int page = 0;//页码从0开始
        int size = 10;
        Pageable pageable = PageRequest.of(page,size);
        Page<CmsPage> all = cmsPageRepository.findAll(pageable);
        System.out.println(all);
    }


    @Test
    public void testInsert(){

    }

    @Test
    public void testUpdate(){
        //查询对象
        Optional<CmsPage> optional = cmsPageRepository.findById("123456789");
        if (optional.isPresent()){//判断是否为空
            CmsPage cmsPage = optional.get();
            //设置要修改的值
            cmsPage.setPageAliase("test01");
            //...
            //修改
            CmsPage save = cmsPageRepository.save(cmsPage);
            System.out.println(save);

        }

        //设置要修改的值

        //提交修改
    }

    @Test
    public void testFindByPageName(){
        CmsPage cmsPage = cmsPageRepository.findByPageName("测试页面");
        System.out.println(cmsPage);
    }


    //自定义条件查询
    @Test
    public void testFindAllByExample(){
        //分页参数
        int page = 0;//页码从0开始
        int size = 10;
        Pageable pageable = PageRequest.of(page,size);

        //条件值对象
        CmsPage cmsPage = new CmsPage();
        //要查询12324564站点的页面
        cmsPage.setSiteId("12324564");
        //设置模板id条件
       // cmsPage.setTemplateId("123456789");
        //设置页面别名
        cmsPage.setPageAliase("轮播");

        //条件匹配器
//        ExampleMatcher exampleMatcher = ExampleMatcher.matching();
//        exampleMatcher = exampleMatcher.withMatcher("pageAliase", ExampleMatcher.GenericPropertyMatchers.contains());
        //ExampleMatcher.GenericPropertyMatchers.contains()   该匹配器表示包含关键字
        ExampleMatcher exampleMatcher = ExampleMatcher.matching()
                .withMatcher("pageAliase", ExampleMatcher.GenericPropertyMatchers.contains());

        //定义Example
        Example<CmsPage> example = Example.of(cmsPage,exampleMatcher);
        Page<CmsPage> all = cmsPageRepository.findAll(example, pageable);
        List<CmsPage> content = all.getContent();
        System.out.println(content);

    }
























}
