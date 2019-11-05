package com.college.entrust.service.impl;

import com.college.common.Exception.EntrustException;
import com.college.common.enums.ExceptionEnum;
import com.college.entrust.mapper.ImageMapper;
import com.college.entrust.pojo.Entrust;
import com.college.entrust.mapper.EntrustMapper;
import com.college.entrust.pojo.Image;
import com.college.entrust.service.EntrustService;
import com.college.entrust.vo.EntrustDetail;
import com.college.entrust.vo.SearchRequest;
import com.github.pagehelper.PageHelper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.List;

@Service
public class EntrustServiceImpl implements EntrustService {

    @Autowired
    private EntrustMapper entrustMapper;

    @Autowired
    private ImageMapper imageMapper;

    @Override
    public EntrustDetail queryEntrustList(SearchRequest request) {
        //分页
        PageHelper.startPage(request.getPage(),request.getSize());
        //过滤
        Example  example=new Example(Entrust.class);
        if(StringUtils.isNotBlank(request.getKey()))
        {
            example.createCriteria().orLike("sub_title","%"+request.getKey()+"%");
        }
        //查询
        List<Entrust> entrusts = entrustMapper.selectByExample(example);
        if(CollectionUtils.isEmpty(entrusts))
        {
            throw new EntrustException(ExceptionEnum.ENTRUST_NOT_FOUND);
        }
        //组装

        EntrustDetail ed=new EntrustDetail();
        for (Entrust entrust : entrusts) {
            //查询传入的照片的集合
            Image image = new Image();
            image.setEntrust_id(entrust.getId());
            List<Image> images = imageMapper.select(image);
            ed.setImages(images);
        }
        ed.setEntrust(entrusts);
        return ed;
    }
}
