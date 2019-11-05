package com.college.entrust.service;

import com.college.entrust.pojo.Entrust;
import com.college.entrust.vo.EntrustDetail;
import com.college.entrust.vo.SearchRequest;

import java.util.List;

public interface EntrustService {
    //查询所有entrust
    EntrustDetail queryEntrustList(SearchRequest searchRequest);
}
