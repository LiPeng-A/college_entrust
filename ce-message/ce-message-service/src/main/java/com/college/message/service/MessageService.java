package com.college.message.service;

import com.college.message.pojo.Message;

import java.util.List;

public interface MessageService {
    //发送通知消息
    void sendMessage(Integer status, Long user_id, Long entrust_id);

    //根据用户id查询有几条新的通知
    Integer queryMessageByUserId(Long user_id);

    //查询用户所有的信息
    List<Message> queryMessageList(Long user_id);

    //根据id查询该通知的所有信息
    Message queryMessageById(Long id);

    //修改通知消息为已查看
    void alertInfo(Long id);

    //根据id删除通知信息
    void deleteMessageById(Long id);
}
