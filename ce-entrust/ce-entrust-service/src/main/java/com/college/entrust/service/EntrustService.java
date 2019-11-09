package com.college.entrust.service;

import com.college.common.vo.PageResult;
import com.college.entrust.pojo.AcceptEntrust;
import com.college.entrust.pojo.Entrust;
import com.college.entrust.vo.SearchRequest;

import java.util.List;

public interface EntrustService {
    //查询所有entrust
    PageResult<Entrust> queryEntrustList(SearchRequest searchRequest);

    //根据id查询当前委托详情
    Entrust queryEntrustById(Long id);

    //接受委托
    void acceptEntrust(Long id);

    //根据用户id查询接受的所有委托
    List<Entrust> queryAcceptEntrust();


    //放弃委托
    void deleteAcceptEntrust(Long id);

    //根据id查询委托详情（带有AcceptEntrust的）
    Entrust queryEntrustDetailById(Long id);

    //确定完成委托
    void confirmEntrust(Long id);
}
