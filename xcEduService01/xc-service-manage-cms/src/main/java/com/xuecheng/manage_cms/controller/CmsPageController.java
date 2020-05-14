package com.xuecheng.manage_cms.controller;

import com.xuecheng.api.cms.CmsPageControllerApi;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.request.QueryPageRequest;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_cms.service.PageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("cms/page")

public class CmsPageController implements CmsPageControllerApi {

    @Autowired
    PageService pageService;

    @Override
    @GetMapping("list/{page}/{size}")//通过url传参接收page和size
    public QueryResponseResult findList(@PathVariable("page") int page, @PathVariable("size")int size, QueryPageRequest queryPageRequest) {


        //暂时使用静态数据测试
//        QueryResult<CmsPage> queryResult = new QueryResult<>();
//        List<CmsPage> list = new ArrayList<>();
//        CmsPage cmsPage = new CmsPage();
//        cmsPage.setPageName("测试页面");
//
//        list.add(cmsPage);
//        queryResult.setList(list);
//        queryResult.setTotal(1);
//        QueryResponseResult queryResponseResult = new QueryResponseResult(CommonCode.SUCCESS,queryResult);
//        return queryResponseResult;

        return pageService.findList(page,size,queryPageRequest);
    }

    @Override
    @PostMapping("/add")
    //将请求过来的json数据转换成对象@RequestBody
    public CmsPageResult add(@RequestBody CmsPage cmsPage) {
        return pageService.add(cmsPage);
    }

    @Override
    @GetMapping("/get/{id}")
    public CmsPage findById(@PathVariable("id") String id) {
        return pageService.getById(id);
    }

    @Override
    @PutMapping("/edit/{id}")
    public CmsPageResult edit(@PathVariable("id") String id, @RequestBody CmsPage cmsPage) {
        return pageService.update(id,cmsPage);
    }

    @Override
    @DeleteMapping("/del/{id}")
    public ResponseResult delete(@PathVariable("id") String id) {
        return pageService.delete(id);
    }

    @Override
    @PostMapping("/postpage/{pageId}")
    public ResponseResult post(@PathVariable("pageId") String pageId) {
        return pageService.post(pageId);
    }


}



















