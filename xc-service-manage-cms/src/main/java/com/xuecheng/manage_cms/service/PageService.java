package com.xuecheng.manage_cms.service;

import com.alibaba.fastjson.JSON;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.xuecheng.framework.domain.cms.CmsConfig;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.CmsSite;
import com.xuecheng.framework.domain.cms.CmsTemplate;
import com.xuecheng.framework.domain.cms.request.QueryPageRequest;
import com.xuecheng.framework.domain.cms.response.CmsCode;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.domain.cms.response.CmsPostPageResult;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_cms.config.RabbitmqConfig;
import com.xuecheng.manage_cms.dao.CmsConfigRepository;
import com.xuecheng.manage_cms.dao.CmsPageRepository;
import com.xuecheng.manage_cms.dao.CmsSiteRepository;
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
    CmsSiteRepository cmsSiteRepository;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    CmsTemplateRepository cmsTemplateRepository;

    @Autowired
    GridFsTemplate gridFsTemplate;

    @Autowired
    GridFSBucket gridFSBucket;

    @Autowired
    RabbitTemplate rabbitTemplate;


    //定义页面的查询方法

    /**
     *
     * @param page 页码从1开始
     * @param size 每页查询数
     * @param queryPageRequest 查询条件
     * @return
     */
    public QueryResponseResult findList(int page, int size, QueryPageRequest queryPageRequest){


        if (queryPageRequest == null){
            queryPageRequest = new QueryPageRequest();
        }
        //自定义条件查询

        //定义条件匹配器
        ExampleMatcher exampleMatcher = ExampleMatcher.matching()
                .withMatcher("pageAliase", ExampleMatcher.GenericPropertyMatchers.contains());

        //条件值对象
        CmsPage cmsPage = new CmsPage();


        //设置条件值（站点id）
        if (StringUtils.isNotEmpty(queryPageRequest.getSiteId())){//使用String的工具类的StringUtils的方法isnotEmpty方法
            cmsPage.setSiteId(queryPageRequest.getSiteId());
        }
        //设置条件值（模板id）
        if (StringUtils.isNotEmpty(queryPageRequest.getTemplateId())){//使用String的工具类的StringUtils的方法isnotEmpty方法
            cmsPage.setTemplateId(queryPageRequest.getTemplateId());
        }

        //设置页面别名作为查询条件（）
        if (StringUtils.isNotEmpty(queryPageRequest.getPageAliase())){//使用String的工具类的StringUtils的方法isnotEmpty方法
            cmsPage.setPageAliase(queryPageRequest.getPageAliase());
        }

        //定义条件查询对象

        Example<CmsPage> example = Example.of(cmsPage,exampleMatcher);
        //分页查询的参数
        if (page<0){//查询第一页
            page = 1;

        }
        page = page -1;
        if (size<0){//查询第一页的记录条数
            size = 10;
        }

        Pageable pageable = PageRequest.of(page,size);
        Page<CmsPage> all = cmsPageRepository.findAll(example,pageable);//自定义条件查询，并且分页
        QueryResult queryResult = new QueryResult();
        queryResult.setList(all.getContent());//获取数据列表
        queryResult.setTotal(all.getTotalElements());//获取数据的总记录数
        QueryResponseResult queryResponseResult = new QueryResponseResult(CommonCode.SUCCESS,queryResult);
        return queryResponseResult;
    }

    //新增页面
    public CmsPageResult add(CmsPage cmsPage){

        if (cmsPage==null){
            //抛出异常，非法参数异常。。指定异常信息
        }
        //校验页面名称，站点ID，页面WebPath的唯一性
        //根据页面名称、站点Id、页面webpath去cms_page集合，如果查到说明此页面已经存在，如果查询不到再继续添加
        CmsPage cmsPage1 = cmsPageRepository.findByPageNameAndSiteIdAndPageWebPath(cmsPage.getPageName(), cmsPage.getSiteId(), cmsPage.getPageWebPath());

        if (cmsPage1!=null){
            //页面已经存在
            //抛出异常
            ExceptionCast.cast(CmsCode.CMS_ADDPAGE_EXISTSNAME);
        }
        //调用dao新增页面
        cmsPage.setPageId(null);//让mongoDB自动生成
        cmsPageRepository.save(cmsPage);
        return new CmsPageResult(CommonCode.SUCCESS,cmsPage);
    }

    //根据id查询页面信息
    public CmsPage getById(String id){
        Optional<CmsPage> optional = cmsPageRepository.findById(id);
        if (optional.isPresent()){
            CmsPage cmsPage = optional.get();
            return cmsPage;
        }
        return null;
    }
    //更新页面信息
    public CmsPageResult update(String id,CmsPage cmsPage){
        //根据id从数据库查询信息
        CmsPage one = this.getById(id);
        if (one!=null){
            //准备更新数据
            //设置要修改的数据库
            //更新模板id
            one.setTemplateId(cmsPage.getTemplateId());
            //更新所属站点
            one.setSiteId(cmsPage.getSiteId());
            //更新页面别名
            one.setPageAliase(cmsPage.getPageAliase());
            //更新页面名称
            one.setPageName(cmsPage.getPageName());
            //更新访问路径
            one.setPageWebPath(cmsPage.getPageWebPath());
            //更新dataUrl
            one.setDataUrl(cmsPage.getDataUrl());
            //更新物理路径
            one.setPagePhysicalPath(cmsPage.getPagePhysicalPath());

            //执行更新
            CmsPage save = cmsPageRepository.save(one);
            if (save!=null){
                //返回成功
                CmsPageResult cmsPageResult = new CmsPageResult(CommonCode.SUCCESS, save);
                return cmsPageResult;
            }
        }
        //返回失败
        return new CmsPageResult(CommonCode.FAIL,null);
    }
    //根据id进行删除
    public ResponseResult delete(String id){
        //先进性查询
        Optional<CmsPage> optional = cmsPageRepository.findById(id);
        if (optional.isPresent()){
            cmsPageRepository.deleteById(id);
            return new ResponseResult(CommonCode.SUCCESS);
        }
        return new ResponseResult(CommonCode.FAIL);
    }


    //根据id查询cmsConfig
    public CmsConfig getConfigById(String id){
        Optional<CmsConfig> optional = cmsConfigRepository.findById(id);
        if (optional.isPresent()){
            CmsConfig cmsConfig = optional.get();
            return cmsConfig;
        }
        return null;
    }

    //页面静态化方法
    public String getPageHtml(String pageId){
        //获取页面模型数据
        Map model = getModelByPageId(pageId);
        if(model == null){
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_DATAISNULL);

        }
        //获取页面模板信息
        String template = getTemplateByPageId(pageId);
        if (StringUtils.isEmpty(template)){
            //模板不为空
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_TEMPLATEISNULL);
        }

        //执行静态化
        String html = generateHtml(template, model);
        if (StringUtils.isEmpty(html)){
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_HTMLISNULL);
        }
        return html;
    }

    //执行静态化
    private String generateHtml(String templateContent,Map model){

        //使用的是freemarker的包
        Configuration configuration = new Configuration(Configuration.getVersion());
        //创建模板加载器
        StringTemplateLoader stringTemplateLoader = new StringTemplateLoader();
        stringTemplateLoader.putTemplate("template",templateContent);

        //向configuration中配置模板加载器
        configuration.setTemplateLoader(stringTemplateLoader);
        //获取模板
        try {
            Template template = configuration.getTemplate("template");
            //调用api实现静态化
            String content = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
            return content;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getTemplateByPageId(String pageId){
        //获取数据模型
        CmsPage cmsPage = this.getById(pageId);
        if (cmsPage == null){
            //页面不存在
            ExceptionCast.cast(CmsCode.CMS_PAGE_NOTEXISTS);
        }

        //获取页面的模板id
        String templateId = cmsPage.getTemplateId();
        if (StringUtils.isEmpty(templateId)){
            //模板不为空
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_TEMPLATEISNULL);
        }
        //查询模板信息
        Optional<CmsTemplate> optional = cmsTemplateRepository.findById(templateId);
        if (optional.isPresent()){
            CmsTemplate cmsTemplate = optional.get();
            //获取模板id
            String templateFileId = cmsTemplate.getTemplateFileId();
            //从gridFS中获取模板内容
            //根据id查询文件
            GridFSFile gridFSFile = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(templateFileId)));
            //打开一个下载流对象
            GridFSDownloadStream gridFSDownloadStream = gridFSBucket.openDownloadStream(gridFSFile.getObjectId());
            //创建GridFSResource对象获取流
            GridFsResource gridFsResource = new GridFsResource(gridFSFile,gridFSDownloadStream);
            //从流中取数据
            try {
                String content = IOUtils.toString(gridFsResource.getInputStream(), "utf-8");
                return content;
            } catch (IOException e) {
                e.printStackTrace();
            }

//            System.out.println(content);
        }
        return null;
    }

    /**
     * 静态化程序获取页面的DataUrl
     * 静态化程序远程请求DataUrl获取数据模型。
     * 静态化程序获取页面的模板信息
     * 执行页面静态化
     * @return
     */
    public Map getModelByPageId(String pageId){
        //获取数据模型
        CmsPage cmsPage = this.getById(pageId);
        if (cmsPage == null){
            //页面不存在
            ExceptionCast.cast(CmsCode.CMS_PAGE_NOTEXISTS);
        }
        //取出页面的dataurl
        String dataUrl = cmsPage.getDataUrl();
        if (StringUtils.isEmpty(dataUrl)){
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_DATAURLISNULL);

        }
        //通过restTemplate请求dataUrl获取数据
        ResponseEntity<Map> forEntity = restTemplate.getForEntity(dataUrl, Map.class);
        Map body = forEntity.getBody();
        return body;
    }

    //页面发布
    public ResponseResult post(String pageId){
        //执行页面静态化
        String pageHtml = this.getPageHtml(pageId);
        //将页面静态化文件存储到GridFs中
        CmsPage cmsPage = saveHtml(pageId, pageHtml);
        //向MQ发消息
        sendPostPage(pageId);
        return new ResponseResult(CommonCode.SUCCESS);
    }

    //向mq发送消息
    private void sendPostPage(String pageId){
        //获取页面信息
        CmsPage cmsPage = this.getById(pageId);
        if (cmsPage==null){
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        //创建消息对象
        HashMap<String, String> map = new HashMap<>();
        map.put("pageId",pageId);
        //转换为json串
        String jsonString = JSON.toJSONString(map);
        //获取到站点id
        String siteId = cmsPage.getSiteId();
        //转发mq
        rabbitTemplate.convertAndSend(RabbitmqConfig.EX_ROUTING_CMS_POSTPAGE,siteId,jsonString);
    }
    //保存html到GridFS
    private CmsPage saveHtml(String pageId,String htmlContent){
        //获取页面信息
        CmsPage cmsPage = this.getById(pageId);
        if (cmsPage==null){
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        ObjectId objectId = null;
        try {
            //将htmlContent转换为输入流
            InputStream inputStream = IOUtils.toInputStream(htmlContent, "utf-8");
            //将html文件保存到GridFS
            objectId = gridFsTemplate.store(inputStream, cmsPage.getPageName());
        } catch (IOException e) {
            e.printStackTrace();
        }
        //将html文件id更新到cmsPage中
        String fileId = objectId.toString();
        cmsPage.setHtmlFileId(fileId);
        cmsPageRepository.save(cmsPage);
        return cmsPage;
    }

    //保存页面，有就更新没有就保存
    public CmsPageResult save(CmsPage cmsPage) {
        //判断页面是否存在
        CmsPage cmsPage1 = cmsPageRepository.findByPageNameAndSiteIdAndPageWebPath(
                                                cmsPage.getPageName(),
                                                cmsPage.getSiteId(),
                                                cmsPage.getPageWebPath());
        if (cmsPage1!=null){
            //进行更新
            return this.update(cmsPage1.getPageId(),cmsPage);
        }
        return this.add(cmsPage);
    }
    //一键发布
    public CmsPostPageResult postPageQuick(CmsPage cmsPage) {

        //将页面信息存储到cms_page集合当中
        CmsPageResult save = this.save(cmsPage);
        if (!save.isSuccess()){
            //抛出异常
            ExceptionCast.cast(CommonCode.FAIL);
        }
        //得到页面的id
        CmsPage saveCmsPage = save.getCmsPage();
        String pageId = save.getCmsPage().getPageId();

        //执行页面发布(先静态化，保存gridFS,向MQ发送消息)
        ResponseResult post = this.post(pageId);
        if (!post.isSuccess()){
            ExceptionCast.cast(CommonCode.FAIL);

        }
        //页面Url= cmsSite.siteDomain+cmsSite.siteWebPath+ cmsPage.pageWebPath + cmsPage.pageName
        //取出站点
        String siteId = saveCmsPage.getSiteId();
        CmsSite cmsSiteById = this.findCmsSiteById(siteId);
        //页面url
        String pageUrl = cmsSiteById.getSiteDomain() + cmsSiteById.getSiteWebPath() + saveCmsPage.getPageWebPath()+saveCmsPage.getPageName();
        return new CmsPostPageResult(CommonCode.SUCCESS,pageUrl);
    }

    //根据站点信息查询站点信息
    public CmsSite findCmsSiteById(String siteId){
        Optional<CmsSite> optional = cmsSiteRepository.findById(siteId);
        if(optional.isPresent()){
            return optional.get();
        }
        return null;
    }
}
