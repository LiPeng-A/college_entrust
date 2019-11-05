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
    private String sub_title;
    private String detail;
    private Date  create_time;
}
