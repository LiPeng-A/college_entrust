package com.college.entrust.pojo;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tb_entrust")
public class Entrust implements Serializable{
    @Id
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    private String title;
    private Integer status; //委托状态 1.未接受 2.已接受 3.已完成 4.委托结束
    private String sub_title;
    private String detail;
    private String image;
    private Date  create_time;
    private Date update_time;
    private Date finish_time;
    private Long user_id;

    @Transient
    private AcceptEntrust acceptEntrust;
}
