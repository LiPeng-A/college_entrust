package com.college.entrust.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tb_entrust")
public class Entrust {
    @Id
    @KeySql(useGeneratedKeys = true)
    private Long id;
    private String title;
    private Integer status; //委托状态 1.未接受 2.已接受 3.已完成 4.委托结束
    private String sub_title;
    private String detail;
    private Date  create_time;
    private Long user_id;
}
