package com.college.message.mq;

import com.college.message.service.MessageService;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Map;

@Component //自动注入
public class MessageListener {

    @Autowired
    private MessageService messageService;


    /**
     * 监听，然后发送通知消息到页面
     * @param paramMap
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "entrust.status.message.queue",
            durable = "true"),
            exchange = @Exchange(name = "ce.entrust.status.exchange",type = ExchangeTypes.TOPIC),
            key = "entrust.status.message"
    ))
    public void listenerSendMsg(Map<String,Object> paramMap){
        if(CollectionUtils.isEmpty(paramMap))
        {
            return;
        }
        //取出状态
        Integer status = (Integer) paramMap.remove("status");
        if(status==null)
        {
            return;
        }
        Long user_id = (Long) paramMap.remove("user_id");
        Long  entrust_id = (Long) paramMap.remove("entrust_id");

        //发送通知消息
        messageService.sendMessage(status,user_id,entrust_id);
    }
}
