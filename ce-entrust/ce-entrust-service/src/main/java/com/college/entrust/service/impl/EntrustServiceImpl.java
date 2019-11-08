package com.college.entrust.service.impl;

import com.college.common.Exception.EntrustException;
import com.college.common.enums.ExceptionEnum;
import com.college.common.vo.PageResult;
import com.college.entrust.pojo.Entrust;
import com.college.entrust.mapper.EntrustMapper;
import com.college.entrust.service.EntrustService;
import com.college.entrust.vo.SearchRequest;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class EntrustServiceImpl implements EntrustService {

    @Autowired
    private EntrustMapper entrustMapper;


    @Override
    public PageResult<Entrust> queryEntrustList(SearchRequest request) {
        //分页
        PageHelper.startPage(request.getPage(),request.getSize());
        //过滤
        Example  example=new Example(Entrust.class);
        if(StringUtils.isNotBlank(request.getFilter()))
        {
            example.createCriteria().orLike("sub_title","%"+request.getFilter()+"%");
        }
        //查询
        List<Entrust> entrusts = entrustMapper.selectByExample(example);
        if(CollectionUtils.isEmpty(entrusts))
        {
            throw new EntrustException(ExceptionEnum.ENTRUST_NOT_FOUND);
        }

        //分页助手解析数据
        PageInfo<Entrust> info = new PageInfo(entrusts);
        Long totalPage=info.getTotal()/request.getSize();
        if(info.getTotal()/request.getSize()!=0)
        {
            totalPage++;
        }
        return new PageResult<>(info.getTotal(),totalPage,info.getList());
    }

    @Override
    public Entrust queryEntrustById(Long id) {
        Entrust entrust = entrustMapper.selectByPrimaryKey(id);
        if(entrust==null)
        {
            throw new EntrustException(ExceptionEnum.ENTRUST_NOT_FOUND);
        }

        return entrust;
    }
}
