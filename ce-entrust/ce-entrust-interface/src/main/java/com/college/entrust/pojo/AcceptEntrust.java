package com.college.entrust.pojo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Data
@Table(name="tb_accept_entrust")
public class AcceptEntrust {
    @Id
    @KeySql(useGeneratedKeys = true)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long entrust_id; //委托编号
    private Date accept_time; // 接受委托时间
    private Long accept_uid; //接受人id
    private Long release_uid; //发布人 id
    private Integer accept_status; //接受人状态
    private Integer release_status; //发布人状态

}
