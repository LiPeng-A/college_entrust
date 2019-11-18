package com.college.message.web;

import com.college.message.pojo.Message;
import com.college.message.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class MessageController {

    @Autowired
    private MessageService messageService;


    /**
     * 发送通知信息
     * @param status
     * @return
     */
    @PostMapping
    public ResponseEntity<Void> sendMessage(@RequestParam("status")Integer status,
                                               @RequestParam("user_id")Long user_id,
                                               @RequestParam("entrust_id")Long entrust_id
                                              ){
        messageService.sendMessage(status,user_id,entrust_id);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 查询当前用户未读信息条数
     * @param user_id
     * @return
     */
    @GetMapping("info/{user_id}")
    public ResponseEntity<Integer> queryMessageByUserId(@PathVariable("user_id")Long user_id){

        return ResponseEntity.ok(messageService.queryMessageByUserId(user_id));
    }

    /**
     * 查询用户的所有通知信息
     * @return
     */
    @GetMapping("list/{user_id}")
    public ResponseEntity<List<Message>> queryMessageList(@PathVariable("user_id")Long user_id){
        return ResponseEntity.ok(messageService.queryMessageList(user_id));
    }

    /**
     * 根据id查询当前通知的所有信息
     * @param id
     * @return
     */
    @GetMapping("detail/{id}")
    public ResponseEntity<Message> queryMessageById(@PathVariable("id")Long id){

        return ResponseEntity.ok(messageService.queryMessageById(id));
    }


    /**
     * 修改通知消息为已查看
     * @param id
     * @return
     */
    @PutMapping("{id}")
    public ResponseEntity<Void> alertInfo(@PathVariable("id")Long id)
    {
        messageService.alertInfo(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * 根据id删除通知信息
     * @param id
     * @return
     */
    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteMessageById(@PathVariable("id")Long id){
        messageService.deleteMessageById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
