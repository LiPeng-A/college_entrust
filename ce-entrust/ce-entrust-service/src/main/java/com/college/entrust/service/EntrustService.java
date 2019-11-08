package com.college.entrust.service;

import com.college.common.vo.PageResult;
import com.college.entrust.pojo.Entrust;
import com.college.entrust.vo.SearchRequest;

public interface EntrustService {
    //查询所有entrust
    PageResult<Entrust> queryEntrustList(SearchRequest searchRequest);

    //根据id查询当前委托详情
    Entrust queryEntrustById(Long id);
}
