package com.xuecheng.manage_cms_client.service;

import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.CmsSite;
import com.xuecheng.manage_cms_client.dao.CmsPageRepository;
import com.xuecheng.manage_cms_client.dao.CmsSiteRepository;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

@Service
public class PageService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PageService.class);


    @Autowired
    GridFSBucket gridFSBucket;

    @Autowired
    GridFsTemplate gridFsTemplate;

    @Autowired
    CmsSiteRepository cmsSiteRepository;
    @Autowired
    CmsPageRepository cmsPageRepository;

    //将页面html保存到页面物理路径
    public void savePageToServerPath(String pageId){
        //根据pageId查询cmsPage
        CmsPage cmsPage = this.findCmsPageById(pageId);
        //得到html的文件id，从cmsPage中获取htmlFileId内容
        String htmlFileId = cmsPage.getHtmlFileId();
        //从gridFs中查询html文件
        InputStream inputStream = this.getFileById(htmlFileId);
        if (inputStream==null){
            LOGGER.error("getFindById inputStream is null,htmlFileId{}"+htmlFileId);
            return;
        }
        //获取站点id
        String siteId = cmsPage.getSiteId();
        //得到站点的信息
        CmsSite cmsSite = this.findCmsSiteById(siteId);
        //得到站点的路径
        String sitePhysicalPath = cmsSite.getSitePhysicalPath();
        //得到页面的物理路径
        String pagePath = sitePhysicalPath+cmsPage.getPagePhysicalPath()+cmsPage.getPageName();
        //将html文件保存到服务器物理路径上
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(new File(pagePath));
            IOUtils.copy(inputStream,fileOutputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    //根据文件id获取文件内容
    public InputStream getFileById(String fileId){
        try {
            //文件对象
            GridFSFile gridFSFile = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(fileId)));
            //打开下载流
            GridFSDownloadStream gridFSDownloadStream = gridFSBucket.openDownloadStream(gridFSFile.getObjectId());
            //定义GridFSResource

            GridFsResource gridFsResource = new GridFsResource(gridFSFile,gridFSDownloadStream);

            return gridFsResource.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }return null;
    }

    //根据站点id得到站点
    public CmsSite findCmsSiteById(String siteId){
        Optional<CmsSite> optional = cmsSiteRepository.findById(siteId);
        if(optional.isPresent()){
            CmsSite cmsSite = optional.get();
            return cmsSite;
        }return null;
    }

    //根据页面id查询页面信息
    public CmsPage findCmsPageById(String pageId){
        Optional<CmsPage> optional = cmsPageRepository.findById(pageId);
        if(optional.isPresent()){
            CmsPage cmsPage = optional.get();
            return cmsPage;
        }return null;
    }

}
