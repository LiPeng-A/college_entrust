package com.college.entrust.pojo;

import lombok.Data;

import javax.persistence.Table;
import java.util.Date;

@Data
@Table(name = "tb_release_Entrust")
public class ReleaseEntrust {
    private Long entrust_id;
    private Date release_time;
}
