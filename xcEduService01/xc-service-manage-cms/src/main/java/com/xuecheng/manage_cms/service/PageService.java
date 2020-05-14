package com.xuecheng.manage_cms.service;

import com.alibaba.fastjson.JSON;
import com.mongodb.client.gridfs.GridFSBucket;

import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.xuecheng.framework.domain.cms.CmsConfig;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.CmsTemplate;
import com.xuecheng.framework.domain.cms.request.QueryPageRequest;
import com.xuecheng.framework.domain.cms.response.CmsCode;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_cms.config.RabbitmqConfig;
import com.xuecheng.manage_cms.dao.CmsConfigRepository;
import com.xuecheng.manage_cms.dao.CmsPageRepository;
import com.xuecheng.manage_cms.dao.CmsTemplateRepository;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class PageService {
    @Autowired
    CmsPageRepository cmsPageRepository;

    @Autowired
    CmsConfigRepository cmsConfigRepository;

    @Autowired
    CmsTemplateRepository cmsTemplateRepository;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    GridFsTemplate gridFsTemplate;

    @Autowired
    GridFSBucket gridFSBucket;

    @Autowired
    RabbitTemplate rabbitTemplate;

    //页面查询

    /**
     *
     * @param page 页码：页码从1开始计数
     * @param size 每页记录数
     * @param queryPageRequest 查询条件
     * @return
     */
    public QueryResponseResult findList(int page,int size, QueryPageRequest queryPageRequest){

        if (queryPageRequest == null){
            queryPageRequest = new QueryPageRequest();
        }


        //自定义条件的查询
        //定义一个条件匹配器
        ExampleMatcher exampleMatcher = ExampleMatcher.matching()
                .withMatcher("pageAliase", ExampleMatcher.GenericPropertyMatchers.contains());
        //条件值对象
        CmsPage cmsPage = new CmsPage();
        //设置条件值(站点id)
        if (StringUtils.isNotEmpty(queryPageRequest.getSiteId())){
            cmsPage.setSiteId(queryPageRequest.getSiteId());
        }
        //设置条件值(模板id)作为查询条件
        if (StringUtils.isNotEmpty(queryPageRequest.getTemplateId())){
            cmsPage.setTemplateId(queryPageRequest.getTemplateId());
        }
        //设置页面别名作为查询条件
        if (StringUtils.isNotEmpty(queryPageRequest.getPageAliase())){
            cmsPage.setPageAliase(queryPageRequest.getPageAliase());
        }
        //定义条件对象Example
        Example<CmsPage> example = Example.of(cmsPage,exampleMatcher);



        if (page <= 0){
            page = 1;
        }
        page = page - 1;
        if (size<=1){
            size=10;
        }

        Pageable pageable = PageRequest.of(page,size);
        Page<CmsPage> all = cmsPageRepository.findAll(example,pageable);//自定义条件查询并且实现分页查询
        QueryResult queryResult = new QueryResult();
        queryResult.setList(all.getContent());//数据列表
        queryResult.setTotal(all.getTotalElements());//数据总记录数
        QueryResponseResult queryResponseResult = new QueryResponseResult(CommonCode.SUCCESS,queryResult);
        return queryResponseResult;
    }


    //新增页面
    public CmsPageResult add(CmsPage cmsPage){

        if (cmsPage == null){
            //抛出异常，非法参数异常...指定异常信息的内容

        }

        //校验页面名称、站点id、页面webpath的唯一性
        //根据页面名称、站点id、页面webpath去cms_page集合，如果查到说明此页面已经存在，如果查询不到再继续添加
        CmsPage cmsPage1 = cmsPageRepository.findByPageNameAndSiteIdAndPageWebPath(cmsPage.getPageName(), cmsPage.getSiteId(), cmsPage.getPageWebPath());
        if(cmsPageRepository!=null){
            //页面已经存在，抛出异常，异常内容就是页面已经存在
            //throw new CustomException(CommonCode.FAIL);
            ExceptionCast.cast(CmsCode.CMS_ADDPAGE_EXISTSNAME);
        }


           //调用dao新增页面
           cmsPage.setPageId(null);//防止别人恶意注入主键
           //CmsPage save = cmsPageRepository.save(cmsPage);
           cmsPageRepository.save(cmsPage);//返回的save和cmsPage是一个对象
           return new CmsPageResult(CommonCode.SUCCESS,cmsPage);


       //提交添加失败
       // return new CmsPageResult(CommonCode.FAIL,null);

    }

    //根据页面id查询页面
    public CmsPage getById(String id){

        Optional<CmsPage> optional = cmsPageRepository.findById(id);
        if (optional.isPresent()){
            CmsPage cmsPage = optional.get();
            return cmsPage;
        }
        return null;
    }



    //修改页面
    public CmsPageResult update(String id,CmsPage cmsPage){
        //根据id从数据库查询页面信息
        CmsPage cmsPage1 = this.getById(id);
        if (cmsPage1 != null){
            //准备更新
            //更新模板id
            cmsPage1.setTemplateId(cmsPage.getTemplateId());
            //更新所属站点
            cmsPage1.setSiteId(cmsPage.getSiteId());
            //更新页面别名
            cmsPage1.setPageAliase(cmsPage.getPageAliase());
            //更新页面名称
            cmsPage1.setPageName(cmsPage.getPageName());
            //更新访问路径
            cmsPage1.setPageWebPath(cmsPage.getPageWebPath());
            //更新物理路径
            cmsPage1.setPagePhysicalPath(cmsPage.getPagePhysicalPath());

            cmsPage1.setDataUrl(cmsPage.getDataUrl());

            //提交修改
            cmsPageRepository.save(cmsPage1);
            return new CmsPageResult(CommonCode.SUCCESS,cmsPage1);
        }
        //修改失败
        return new CmsPageResult(CommonCode.FAIL,null);
    }

    //根据id删除页面
    public ResponseResult delete(String id){
        //先查询再删除
        Optional<CmsPage> optional = cmsPageRepository.findById(id);
        if (optional.isPresent()){
            cmsPageRepository.deleteById(id);
            return new ResponseResult(CommonCode.SUCCESS);
        }
        return new ResponseResult(CommonCode.FAIL);
    }


    //根据id查询CmsConfig
    public CmsConfig getConfigById(String id){

        Optional<CmsConfig> optional = cmsConfigRepository.findById(id);
        if(optional.isPresent()){
            CmsConfig cmsConfig = optional.get();
            return cmsConfig;
        }
        return null;
    }



    //页面静态化方法
    public String getPathHtml(String pageId){

        //获取数据模型
        Map model = getModelByPageId(pageId);
        if (model == null){
            //数据模型获取不到
        }

        //获取页面的模板信息
        String template = getTemplateByPageId(pageId);
        if(StringUtils.isEmpty(template)){
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_TEMPLATEISNULL);
        }

        //执行静态化
        String html = generateHtml(template, model);
        return html;

    }

    //执行静态化,模板和模型数据

    private String generateHtml(String templateContent,Map model){
        //创建配置对象
        Configuration configuration = new Configuration(Configuration.getVersion());
        //创建模板加载器
        StringTemplateLoader stringTemplateLoader = new StringTemplateLoader();
        stringTemplateLoader.putTemplate("tempalte",templateContent);
        //向configuration中配置模板加载器
        configuration.setTemplateLoader(stringTemplateLoader);

        //获取模板内容
        try {
            Template template = configuration.getTemplate("template");
            //调用api进行静态化
            String content = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
            return content;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;

    }

    //获取页面的模板信息
    private String getTemplateByPageId(String pageId){
        //取出页面的信息
        CmsPage cmsPage = this.getById(pageId);
        if (cmsPage == null){
            //页面不存在
            ExceptionCast.cast(CmsCode.CMS_PAGE_NOTEXISTS);
        }
        //获取页面的模板Id
        String templateId = cmsPage.getTemplateId();
        if(StringUtils.isEmpty(templateId)){
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_TEMPLATEISNULL);
        }
        //查询模板信息
        Optional<CmsTemplate> optional = cmsTemplateRepository.findById(templateId);
        if (optional.isPresent()){
            CmsTemplate cmsTemplate = optional.get();
            //获取模板文件id
            String templateFileId = cmsTemplate.getTemplateFileId();
            //从GridFS中取模板文件内容
            //根据文件id查询文件
            GridFSFile gridFSFile = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(templateFileId)));

            //打开一个下载流对象
            GridFSDownloadStream gridFSDownloadStream = gridFSBucket.openDownloadStream(gridFSFile.getObjectId());
            //创建GridFsResource对象，获取流
            GridFsResource gridFsResource = new GridFsResource(gridFSFile,gridFSDownloadStream);
            //从流中取数据
            try {
                String content = IOUtils.toString(gridFsResource.getInputStream(), "utf-8");
                return content;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;

    }


    //获取数据模型
    private Map getModelByPageId(String pageId){
        //取出页面的信息
        CmsPage cmsPage = this.getById(pageId);
        if (cmsPage == null){
            //页面不存在
            ExceptionCast.cast(CmsCode.CMS_PAGE_NOTEXISTS);
        }
        //取出页面的dateUrl
        String dateUrl = cmsPage.getDataUrl();
        if (dateUrl == null){
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_DATAURLISNULL);
        }
        //通过restTemplate请求dataUrl获取数据
        ResponseEntity<Map> forEntity = restTemplate.getForEntity(dateUrl, Map.class);
        Map body = forEntity.getBody();
        return body;

    }


    //页面发布
    public ResponseResult post(String pageId){
        //执行页面静态化
        String pageHtml = this.getPathHtml(pageId);
        //将页面静态化文件存储到GridFS中
        CmsPage cmsPage = saveHtml(pageId, pageHtml);
        //向mq发消息
        sendPostPage(pageId);
        return new ResponseResult(CommonCode.SUCCESS);

    }


    //向mq发送消息
    private void sendPostPage(String pageId){

        //得到页面信息
        CmsPage cmsPage = this.getById(pageId);
        if (cmsPage == null){
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }

        //拼装消息对象
        Map<String,String> msg = new HashMap<>();
        msg.put("pageId",pageId);
        //转成json串
        String jsonString = JSON.toJSONString(msg);
        //发送给mq
        //站点id
        String siteId = cmsPage.getSiteId();
        //指定交换机,siteId（routingKey）发送消息
        rabbitTemplate.convertAndSend(RabbitmqConfig.EX_ROUTING_CMS_POSTPAGE,siteId,jsonString);

    }

    //保存html到GridFS
    private  CmsPage saveHtml(String pageId,String htmlContent){

        //先得到页面的信息
        CmsPage cmsPage = this.getById(pageId);
        if (cmsPage == null){
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        String pageName = cmsPage.getPageName();
        ObjectId objectId = null;
        try {
            //将htmlContent转成输入流
            InputStream inputStream = IOUtils.toInputStream(htmlContent, "utf-8");
            //将html文件内容保存到GridFS中
            objectId = gridFsTemplate.store(inputStream, pageName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //把html文件id更新到cmsPage中
        cmsPage.setHtmlFileId(objectId.toHexString());
        cmsPageRepository.save(cmsPage);
        return cmsPage;
    }























}
