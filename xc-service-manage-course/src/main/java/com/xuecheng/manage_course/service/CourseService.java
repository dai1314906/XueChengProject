package com.xuecheng.manage_course.service;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.domain.cms.response.CmsPostPageResult;
import com.xuecheng.framework.domain.course.*;
import com.xuecheng.framework.domain.course.ext.CourseInfo;
import com.xuecheng.framework.domain.course.ext.CourseView;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.framework.domain.course.request.CourseListRequest;
import com.xuecheng.framework.domain.course.response.CourseCode;
import com.xuecheng.framework.domain.course.response.CoursePublishResult;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_course.client.CmsPageClient;
import com.xuecheng.manage_course.dao.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class CourseService {
    @Autowired
    TeachplanRepository teachplanRepository;
    @Autowired
    CourseBaseRepository courseBaseRepository;

    @Autowired
    TeachplanMediaRepository teachplanMediaRepository;
    @Autowired
    TeachplanMapper teachplanMapper;

    @Autowired
    CourseMapper  courseMapper;

    @Autowired
    CoursePicRepository coursePicRepository;

    @Autowired
    CourseMarketRepository courseMarketRepository;

    @Autowired
    CoursePubRepository coursePubRepository;

    @Autowired
    TeachplanMediaPubRepository teachplanMediaPubRepository;


    @Autowired
    CmsPageClient cmsPageClient;


    @Value("${course‐publish.dataUrlPre}")
    private String publish_dataUrlPre;
    @Value("${course‐publish.pagePhysicalPath}")
    private String publish_page_physicalpath;
    @Value("${course‐publish.pageWebPath}")
    private String publish_page_webpath;
    @Value("${course‐publish.siteId}")
    private String publish_siteId;
    @Value("${course‐publish.templateId}")
    private String publish_templateId;
    @Value("${course‐publish.previewUrl}")
    private String previewUrl;


    //课程计划查询
    public TeachplanNode findTeachplanList(String courseId){
        return teachplanMapper.selectList(courseId);
    }
    //添加课程计划
    @Transactional
    public ResponseResult addTeachplan(Teachplan teachplan) {
        if (teachplan ==null ||
                StringUtils.isEmpty(teachplan.getCourseid()) ||
                StringUtils.isEmpty(teachplan.getPname())){
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        String courseid = teachplan.getCourseid();
        //处理parentId
        String parentid = teachplan.getParentid();
        if (StringUtils.isEmpty(parentid)){
            //取出该课程的根节点
            parentid = this.getTeachplanRoot(courseid);
        }

        Optional<Teachplan> optional = teachplanRepository.findById(parentid);
        Teachplan parentNode = optional.get();
        //父节点的级别
        String grade = parentNode.getGrade();

        //新节点
        Teachplan teachplanNew = new Teachplan();
        //将页面提交的teachplan信息拷贝到teachplanNew对象中
        BeanUtils.copyProperties(teachplan,teachplanNew);
        teachplanNew.setParentid(parentid);
        teachplanNew.setCourseid(courseid);
        if (grade.equals("1")){
            teachplanNew.setGrade("2");//级别，根据父节点的级别来设置
        }else {
            teachplanNew.setGrade("3");
        }

        teachplanRepository.save(teachplanNew);

        return new ResponseResult(CommonCode.SUCCESS);
    }

    //查询课程的根节点，如果查询不到则自动添加根节点
    private String getTeachplanRoot(String courseId){
        Optional<CourseBase> optional = courseBaseRepository.findById(courseId);
        if (!optional.isPresent()){
            return null;
        }
        //课程信息
        CourseBase courseBase = optional.get();

        //查询课程的根节点
        List<Teachplan> teachplanList = teachplanRepository.findByCourseidAndParentid(courseId, "0");
        if (teachplanList == null || teachplanList.size()<=0){
            //查询不到，要自动添加根节点
            Teachplan teachplan = new Teachplan();
            teachplan.setParentid("0");
            teachplan.setParentid("1");
            teachplan.setPname(courseBase.getName());
            teachplan.setCourseid(courseId);
            teachplan.setStatus("0");
            teachplanRepository.save(teachplan);
            return teachplan.getId();
        }
        //返回根节点的id
        return teachplanList.get(0).getId();
    }
    //向课程管理数据添加课程与图片的关联信息
    @Transactional
    public ResponseResult addCoursePic(String courseId, String pic) {
        //课程图片信息
        CoursePic coursePic = null;
        //查询课程图片
        Optional<CoursePic> picOptional = coursePicRepository.findById(courseId);
        if (!picOptional.isPresent()){
            coursePic = picOptional.get();
        }
        if (coursePic == null){
            coursePic = new CoursePic();
        }
        coursePic.setPic(pic);
        coursePic.setCourseid(courseId);
        coursePicRepository.save(coursePic);
        return new ResponseResult(CommonCode.SUCCESS);
    }
    //查询课程图片
    public CoursePic findCoursePic(String courseId) {
        //查询课程图片
        Optional<CoursePic> picOptional = coursePicRepository.findById(courseId);
        if (picOptional.isPresent()){
            CoursePic coursePic = picOptional.get();
            return coursePic;
        }
        return null;
    }
    //删除课程图片
    @Transactional
    public ResponseResult deleteCoursePic(String courseId) {
        //执行删除
        long result = coursePicRepository.deleteByCourseid(courseId);
        if (result>0){
            return new ResponseResult(CommonCode.SUCCESS);
        }
        return new ResponseResult(CommonCode.FAIL);
    }
    //查询课程视图,包括基本信息、图片、营销、课程计划
    public CourseView getCourseView(String id) {
        CourseView courseView = new CourseView();
        //查询课程的基本信息
        Optional<CourseBase> courseBaseOptional = courseBaseRepository.findById(id);
        if (courseBaseOptional.isPresent()){
            CourseBase courseBase = courseBaseOptional.get();
            courseView.setCourseBase(courseBase);
        }

        Optional<CoursePic> picOptional = coursePicRepository.findById(id);
        if (picOptional.isPresent()){
            CoursePic coursePic = picOptional.get();
            courseView.setCoursePic(coursePic);
        }

        //课程营销信息
        Optional<CourseMarket> marketOptional = courseMarketRepository.findById(id);
        if (marketOptional.isPresent()){
            CourseMarket courseMarket = marketOptional.get();
            courseView.setCourseMarket(courseMarket);
        }
        //课程计划信息
        TeachplanNode teachplanNode = teachplanMapper.selectList(id);
        courseView.setTeachplanNode(teachplanNode);

        return courseView;
    }
    //根据id查询课程基本信息
    public CourseBase findCourseBaseById(String courseId){
        Optional<CourseBase> baseOptional = courseBaseRepository.findById(courseId);
        if (baseOptional.isPresent()){
            CourseBase courseBase = baseOptional.get();
            return courseBase;
        }
        ExceptionCast.cast(CourseCode.COURSE_GET_NOTEXISTS);
        return null;
    }

    //课程预览
    public CoursePublishResult preview(String id) {
        CourseBase courseBaseById = this.findCourseBaseById(id);

        //请求cms添加页面
        //准备cmsPage信息
        CmsPage cmsPage = new CmsPage();
        cmsPage.setSiteId(publish_siteId);//站点id
        cmsPage.setDataUrl(publish_dataUrlPre+id); //数据模型的url
        cmsPage.setPageName(id+".html");//页面名称
        cmsPage.setPageAliase(courseBaseById.getName());//页面别名,就是课程名称
        cmsPage.setPagePhysicalPath(publish_page_physicalpath);//页面物理路径
        cmsPage.setPageWebPath(publish_page_webpath); //页面webpath
        cmsPage.setTemplateId(publish_templateId);//页面模板id



        //远程调用cms
        CmsPageResult cmsPageResult = cmsPageClient.saveCmaPage(cmsPage);
        if (!cmsPageResult.isSuccess()){
            //抛出异常
            return new CoursePublishResult(CommonCode.FAIL,null);
        }
        //获取页面
        CmsPage cmsPage1 = cmsPageResult.getCmsPage();
        String pageId = cmsPage1.getPageId();

        //拼装页面的url
        String url = previewUrl+pageId;
        //返回CoursePublishResult对象(这里面已经包含了页面预览的url)
        return new CoursePublishResult(CommonCode.SUCCESS,url);
    }
    //课程发布
    @Transactional
    public CoursePublishResult publish(String id) {
        CourseBase courseBaseById = this.findCourseBaseById(id);
        //准备页面的信息
        CmsPage cmsPage = new CmsPage();
        cmsPage.setSiteId(publish_siteId);//站点id
        cmsPage.setDataUrl(publish_dataUrlPre+id); //数据模型的url
        cmsPage.setPageName(id+".html");//页面名称
        cmsPage.setPageAliase(courseBaseById.getName());//页面别名,就是课程名称
        cmsPage.setPagePhysicalPath(publish_page_physicalpath);//页面物理路径
        cmsPage.setPageWebPath(publish_page_webpath); //页面webpath
        cmsPage.setTemplateId(publish_templateId);//页面模板id

        //调用cms一键发布接口将课程详情页面发布到服务器
        CmsPostPageResult cmsPostPageResult = cmsPageClient.postPageQuick(cmsPage);
        if (cmsPostPageResult.isSuccess()){
            return new CoursePublishResult(CommonCode.FAIL,null);
        }
        //保存课程的发布状态为“已发布”
        CourseBase courseBase = this.saveCoursePubState(id);
        if (courseBase == null){
            return new CoursePublishResult(CommonCode.FAIL,null);
        }
        //保存课程索引信息
        //创建一个coursePub对象
        CoursePub coursePub = createCoursePub(id);
        //将coursePub保存到数据库
        saveCoursePub(id,coursePub);
        //缓存课程的信息
        String pageUrl = cmsPostPageResult.getPageUrl();
        //向teachplanMediaPub中保存课程媒资信息
        saveTeachplanMediaPub(id);

        return new CoursePublishResult(CommonCode.SUCCESS,pageUrl);
    }
    //向teachplanMediaPub中保存媒资信息
    private void saveTeachplanMediaPub(String courseId){
        //先删除TeachplanMediaPub的数据
        teachplanMediaPubRepository.deleteByCourseId(courseId);
        //从taechMedia中查询
        List<TeachplanMedia> teachplanMediaList = teachplanMediaRepository.findByCourseId(courseId);

        List<TeachplanMediaPub> teachplanMediaPubs = new ArrayList<>();
        for (TeachplanMedia teachplanMedia : teachplanMediaList) {
            TeachplanMediaPub teachplanMediaPub = new TeachplanMediaPub();
            BeanUtils.copyProperties(teachplanMedia,teachplanMediaPub);
            //时间戳
            teachplanMediaPub.setTimestamp(new Date());
            teachplanMediaPubs.add(teachplanMediaPub);
        }
        //将teachplanMediaList插入到teachplanMediaPub
        teachplanMediaPubRepository.saveAll(teachplanMediaPubs);
    }

    //将coursePub保存到数据库
    private CoursePub saveCoursePub(String id,CoursePub coursePub){
        CoursePub coursePubNew = null;
        //根据课程id查询coursePub
        Optional<CoursePub> coursePubOptional = coursePubRepository.findById(id);
        if (coursePubOptional.isPresent()){
            coursePubNew = coursePubOptional.get();
        }else {
            coursePubNew = new CoursePub();
        }
        //将coursePub对象中的信息保存到CoursePubNew
        BeanUtils.copyProperties(coursePub,coursePubNew);
        coursePubNew.setId(id);
        //时间戳
        coursePubNew.setTimestamp(new Date());
        //发布时间
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYY‐MM‐dd HH:mm:ss");
        String date = simpleDateFormat.format(new Date());
        coursePubNew.setPubTime(date);
        coursePubRepository.save(coursePubNew);
        return coursePubNew;
    }

    ///创建coursePub对象
    private CoursePub createCoursePub(String id){
        CoursePub coursePub = new CoursePub();
        //根据课程id查询course_base
        Optional<CourseBase> baseOptional = courseBaseRepository.findById(id);
        if (baseOptional.isPresent()){
            CourseBase courseBase = baseOptional.get();
            //将courseBase属性拷贝到CoursePub中
            BeanUtils.copyProperties(courseBase,coursePub);
        }

        //查询课程图片
        Optional<CoursePic> picOptional = coursePicRepository.findById(id);
        if(picOptional.isPresent()){
            CoursePic coursePic = picOptional.get();
            BeanUtils.copyProperties(coursePic, coursePub);
        }

        //课程营销信息
        Optional<CourseMarket> marketOptional = courseMarketRepository.findById(id);
        if(marketOptional.isPresent()){
            CourseMarket courseMarket = marketOptional.get();
            BeanUtils.copyProperties(courseMarket, coursePub);
        }

        //课程计划信息
        TeachplanNode teachplanNode = teachplanMapper.selectList(id);
        String jsonString = JSON.toJSONString(teachplanNode);

        //将课程计划信息json串保存到course_pub中
        coursePub.setTeachplan(jsonString);
        return coursePub;

        //根据课程id查询course_base
    }

    //更新课程状态为已发布 202002
    private CourseBase saveCoursePubState(String courseId){
        CourseBase courseBaseById = this.findCourseBaseById(courseId);
        courseBaseById.setStatus("202002");
        courseBaseRepository.save(courseBaseById);
        return courseBaseById;
    }
    //保存课程计划与媒资文件关联
    public ResponseResult savemedia(TeachplanMedia teachplanMedia) {
        if (teachplanMedia==null||StringUtils.isEmpty(teachplanMedia.getTeachplanId())){
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        //校验课程计划是否是三级
        //课程计划
        String teachplanId = teachplanMedia.getTeachplanId();
        //查询课程计划
        Optional<Teachplan> optional = teachplanRepository.findById(teachplanId);
        if (!optional.isPresent()){
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        //查询到教学计划
        Teachplan teachplan = optional.get();
        //获取课程计划等级
        String grade = teachplan.getGrade();
        if (StringUtils.isEmpty(grade)||!grade.equals("3")){
            ExceptionCast.cast(CourseCode.COURSE_MEDIA_TEACHPLAN_GRADEERROR);
        }
        //查询TeachMedia的课程id
        Optional<TeachplanMedia> mediaOptional = teachplanMediaRepository.findById(teachplanId);
        TeachplanMedia one = null;
        if (mediaOptional.isPresent()){
            one = mediaOptional.get();
        }else {
            one = new TeachplanMedia();
        }
        //将teachplanMedia保存到数据库中
        one.setCourseId(teachplan.getCourseid());
        one.setMediaId(teachplanMedia.getMediaId());
        one.setMediaFileOriginalName(teachplanMedia.getMediaFileOriginalName());
        one.setMediaUrl(teachplanMedia.getMediaUrl());
        one.setTeachplanId(teachplanId);
        teachplanMediaRepository.save(one);

        return new ResponseResult(CommonCode.SUCCESS);
    }
    //查询我的课程
    public QueryResponseResult<CourseInfo> findCourseList(String company_id, int page, int size, CourseListRequest courseListRequest) {
        if (courseListRequest == null){
            courseListRequest  = new CourseListRequest();
        }
        //将公司的id传入dao
        courseListRequest.setCompanyId(company_id);
        //分页
        PageHelper.startPage(page, size);
        Page<CourseInfo> courseListPage = courseMapper.findCourseListPage(courseListRequest);
        List<CourseInfo> list = courseListPage.getResult();
        long total = courseListPage.getTotal();
        QueryResult<CourseInfo> courseIncfoQueryResult = new QueryResult<CourseInfo>();
        courseIncfoQueryResult.setList(list);
        courseIncfoQueryResult.setTotal(total);
        return new QueryResponseResult<CourseInfo>(CommonCode.SUCCESS,courseIncfoQueryResult);
    }
}
