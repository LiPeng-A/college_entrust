package com.college.entrust.pojo;

import lombok.Data;

import javax.persistence.Table;
import java.util.Date;

@Data
@Table(name="tb_accept_entrust")
public class AcceptEntrust {
    private Long entrust_id;
    private Date accept_time;
}
