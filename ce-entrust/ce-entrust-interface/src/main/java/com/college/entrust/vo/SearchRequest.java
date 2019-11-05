package com.college.entrust.vo;

import lombok.Data;

@Data
public class SearchRequest {

    private String key;
    private Integer page;
    private Integer size;
}
