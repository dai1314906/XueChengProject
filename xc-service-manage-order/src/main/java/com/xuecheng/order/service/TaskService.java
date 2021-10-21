package com.xuecheng.order.service;

import com.xuecheng.framework.domain.task.XcTask;
import com.xuecheng.framework.domain.task.XcTaskHis;
import com.xuecheng.order.dao.XcTaskHisRepository;
import com.xuecheng.order.dao.XcTaskRepository;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    @Autowired
    XcTaskRepository xcTaskRepository;

    @Autowired
    XcTaskHisRepository xcTaskHisRepository;

    @Autowired
    RabbitTemplate rabbitTemplate;

    //查询前n条任务
    public List<XcTask> findXcTaskList(Date updateTime,int size){
        //设置分页参数
        Pageable pageable = new PageRequest(0,size);
        //注意使用springframework.data.domain的包
        Page<XcTask> all = xcTaskRepository.findByUpdateTimeBefore(pageable,updateTime);

        List<XcTask> list = all.getContent();
        return list;
    }

    //发布消息
    @Transactional
    public void publish( XcTask xcTask,String ex,String routingKey){
        Optional<XcTask> optional = xcTaskRepository.findById(xcTask.getId());
        if (optional.isPresent()){
            rabbitTemplate.convertAndSend(ex,routingKey,xcTask);
            //更新任务的时间
            XcTask one = optional.get();
            one.setUpdateTime(new Date());
            xcTaskRepository.save(one);
        }
    }

    //获取任务
    @Transactional
    public int getTask(String id,int version){
        //通过乐观锁的方式来获取更新数据表，如果说大于零则证明取到任务
        int count =xcTaskRepository.updateTaskVersion(id,version);
        return count;
    }

    //完成任务
    @Transactional
    public void finishTask(String taskId){
        Optional<XcTask> optional = xcTaskRepository.findById(taskId);
        if (optional.isPresent()){

            //当前任务
            XcTask xcTask = optional.get();
            //历史任务
            XcTaskHis xcTaskHis = new XcTaskHis();
            BeanUtils.copyProperties(xcTask,xcTaskHis);

            xcTaskHisRepository.save(xcTaskHis);

            xcTaskRepository.delete(xcTask);
        }
    }
}
