package com.college.message.pojo;

import com.college.entrust.pojo.Entrust;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;

@Data
@Table(name = "tb_message")
public class Message {
    @Id
    @KeySql(useGeneratedKeys = true)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id; //通知 id

    private String msg; //通知消息

    private Date time; //通知发送时间

    private Long user_id; //接受人id

    private Integer is_read; //是否已读

    private Long entrust_id; //委托id

    @Transient
    private Entrust entrust;
}
