package com.college.entrust.vo;

import lombok.Data;

@Data
public class SearchRequest {

    private String filter;
    private Integer page;
    private Integer size;
}
