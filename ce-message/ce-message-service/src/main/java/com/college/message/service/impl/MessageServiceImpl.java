package com.college.message.service.impl;

import com.college.common.Exception.EntrustException;
import com.college.common.enums.ExceptionEnum;
import com.college.common.enums.MessageEnum;
import com.college.common.utils.IdWorker;
import com.college.entrust.pojo.Entrust;
import com.college.message.client.EntrustClient;
import com.college.message.mapper.MessageMapper;
import com.college.message.pojo.Message;
import com.college.message.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private EntrustClient entrustClient;

    /**
     * 发送通知信息
     * @param status
     * @return
     */
    @Override
    public void sendMessage(Integer status,Long user_id,Long entrust_id) {
        //设置message中的属性
        Message msg=new Message();
        msg.setId(System.currentTimeMillis());
        msg.setTime(new Date());
        msg.setUser_id(user_id);
        msg.setIs_read(0);
        msg.setEntrust_id(entrust_id);
        //判断获取的委托的状态
        switch (status)
        {
            //根据委托的状态发送指定的消息
            case 1: msg.setMsg(MessageEnum.ACCEPTED_BY_OTHER.getValue());
            break;
            case 2:msg.setMsg(MessageEnum.CONFIRM_BY_OTHER.getValue());
            break;
            case 3:msg.setMsg(MessageEnum.FINISH_BY_OTHER.getValue());
            break;
            case 4:msg.setMsg(MessageEnum.ENTRUST_OVER.getValue());
            break;
            case -1:msg.setMsg(MessageEnum.ABANDON_ENTRUST.getValue());
            break;
        }
        //将对应的消息写入 数据库
        int i = messageMapper.insertSelective(msg);
        if(i!=1)
        {
            throw new EntrustException(ExceptionEnum.SEND_MESSAGE_ERROR);
        }
    }

    @Override
    public Integer queryMessageByUserId(Long user_id) {
        //查询通知信息
        Message msg=new Message();
        msg.setUser_id(user_id);
        //根据用户id查询该用户的未读信息
        List<Message> messageList = messageMapper.select(msg);
        if(CollectionUtils.isEmpty(messageList))
        {
            return 0;
        }
        //收集id
        List<Integer> is_read = messageList.stream().map(message -> message.getIs_read()).collect(Collectors.toList());
        Integer num=0;
        for (Integer integer : is_read) {
            if(integer==0)
            {
                num++;
            }
        }

        return num;
    }

    //查询用户的所有通知信息
    @Override
    public List<Message> queryMessageList(Long user_id) {
        //根据用户id查
        Message msg=new Message();
        msg.setUser_id(user_id);
        List<Message> messageList = messageMapper.select(msg);
        if(CollectionUtils.isEmpty(messageList)){
            throw new EntrustException(ExceptionEnum.MESSAGE_NOT_FOUND);
        }
        return messageList;
    }

    //根据id查询该通知信息的详情
    @Override
    public Message queryMessageById(Long id) {
        Message message = messageMapper.selectByPrimaryKey(id);
        if(message==null)
        {
            throw new EntrustException(ExceptionEnum.MESSAGE_NOT_FOUND);
        }
        Long entrust_id = message.getEntrust_id();
        Entrust entrust = entrustClient.queryEntrustDetailById(entrust_id);
        if(entrust==null)
        {
            throw new EntrustException(ExceptionEnum.ENTRUST_NOT_FOUND);
        }
        message.setEntrust(entrust);
        return message;
    }

    @Override
    public void alertInfo(Long id) {
        Message message = new Message();
        message.setId(id);
        message.setIs_read(1);
        int i = messageMapper.updateByPrimaryKeySelective(message);
        if(i!=1)
        {
            throw new EntrustException(ExceptionEnum.UPDATE_MESSAGE_ERROR);
        }
    }

    @Override
    public void deleteMessageById(Long id) {
        int i = messageMapper.deleteByPrimaryKey(id);
        if(i!=1)
        {
            throw new EntrustException(ExceptionEnum.DELETE_ENTRUST_ERROR);
        }

    }
}
