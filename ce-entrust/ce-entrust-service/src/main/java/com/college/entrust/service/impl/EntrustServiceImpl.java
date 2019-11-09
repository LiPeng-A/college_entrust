package com.college.entrust.service.impl;

import com.college.auth.pojo.UserInfo;
import com.college.common.Exception.EntrustException;
import com.college.common.enums.ExceptionEnum;
import com.college.common.vo.PageResult;
import com.college.entrust.interceptor.UserInterceptor;
import com.college.entrust.mapper.AcceptEntrustMapper;
import com.college.entrust.mapper.EntrustMapper;
import com.college.entrust.pojo.AcceptEntrust;
import com.college.entrust.pojo.Entrust;
import com.college.entrust.service.EntrustService;
import com.college.entrust.vo.SearchRequest;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class EntrustServiceImpl implements EntrustService {

    @Autowired
    private EntrustMapper entrustMapper;

    @Autowired
    private AcceptEntrustMapper acceptEntrustMapper;



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

    @Transactional
    @Override
    public void acceptEntrust(Long id) {
        //1.将原先的委托的状态置为已接受
        Entrust entrust=new Entrust();
        entrust.setStatus(2);  //将委托状态置为已接受
        entrust.setId(id);
        entrustMapper.updateByPrimaryKeySelective(entrust);
        //2.将当前的用户信息与委托id存入接受委托的表中，将状态置为接受
        Entrust en = entrustMapper.selectOne(entrust);
        AcceptEntrust acceptEntrust = new AcceptEntrust();
        //拼装数据
        acceptEntrust.setAccept_status(1); //接受状态
        acceptEntrust.setAccept_time(new Date()); //接受时间
        acceptEntrust.setEntrust_id(en.getId()); //委托id
        acceptEntrust.setRelease_uid(en.getUser_id()); //发布人id
        acceptEntrust.setRelease_status(0); //对方状态为 未确认
        //获取登录的用户的信息
        UserInfo user = UserInterceptor.getUser();
        acceptEntrust.setAccept_uid(user.getId()); //接受人id
        //保存接受的临时信息到数据库
        int insert = acceptEntrustMapper.insert(acceptEntrust);
        if(insert!=1)
        {
            log.error("[委托服务]  接受委托失败，失败用户：{}",user.getUsername());
            throw new EntrustException(ExceptionEnum.ACCEPT_ENTRUST_ERROR);
        }
    }

    @Override
    public List<Entrust> queryAcceptEntrust() {

        //获取当前登录用户
        UserInfo user = UserInterceptor.getUser();
        //查询接受的委托
        AcceptEntrust acceptEntrust = new AcceptEntrust();
        acceptEntrust.setAccept_uid(user.getId());
        List<AcceptEntrust> acceptEntrustList = acceptEntrustMapper.select(acceptEntrust);
        if (CollectionUtils.isEmpty(acceptEntrustList)) {
            throw new EntrustException(ExceptionEnum.ACCEPT_ENTRUST_NOT_FOUND);
        }
        //收集委托的id集合
        List<Long> ids = acceptEntrustList.stream().map(accept -> accept.getEntrust_id()).collect(Collectors.toList());
        //根据id集合查询 委托集合
        List<Entrust> entrusts = entrustMapper.selectByIdList(ids);
        //判断是否为空
        if (CollectionUtils.isEmpty(entrusts)) {
            throw new EntrustException(ExceptionEnum.ENTRUST_NOT_FOUND);
        }
        List<Entrust> entrusts1 = new ArrayList<>();
        for (Entrust entrust : entrusts) {
            for (AcceptEntrust acceptEntrust1 : acceptEntrustList) {
                if (acceptEntrust1.getEntrust_id() == entrust.getId()) {
                    entrust.setAcceptEntrust(acceptEntrust1);
                    entrusts1.add(entrust);
                }
            }
        }
        //返回entrust集合
        return entrusts1;
    }

    @Transactional
    @Override
    public void deleteAcceptEntrust(Long id) {
        //删除委托
        AcceptEntrust acceptEntrust=new AcceptEntrust();
        acceptEntrust.setEntrust_id(id);
        int i = acceptEntrustMapper.delete(acceptEntrust);
        if(i!=1)
        {
            throw new EntrustException(ExceptionEnum.DELETE_ACCEPT_ENTRUST_ERROR);
        }
        //改变委托状态
        Entrust entrust = new Entrust();
        entrust.setId(id);
        entrust.setStatus(1);
        int i1 = entrustMapper.updateByPrimaryKeySelective(entrust);
        if(i1!=1)
        {
            throw new EntrustException(ExceptionEnum.ENTRUST_STATUS_UPDATE_FAILED);
        }
    }


    @Override
    public Entrust queryEntrustDetailById(Long id) {

        Entrust entrust = entrustMapper.selectByPrimaryKey(id);
        if(entrust==null)
        {
            throw new EntrustException(ExceptionEnum.ENTRUST_NOT_FOUND);
        }
        AcceptEntrust acceptEntrust2=new AcceptEntrust();
        acceptEntrust2.setEntrust_id(id);
        AcceptEntrust acceptEntrust = acceptEntrustMapper.selectOne(acceptEntrust2);
        if(acceptEntrust==null)
        {
            throw new EntrustException(ExceptionEnum.ACCEPT_ENTRUST_NOT_FOUND);
        }
        entrust.setAcceptEntrust(acceptEntrust);
        return entrust;
    }

    @Transactional
    @Override
    public void confirmEntrust(Long id) {
        //改变委托的状态
        Entrust entrust = new Entrust();
        entrust.setStatus(3);
        entrust.setId(id);
        entrust.setUpdate_time(new Date());
        int i = entrustMapper.updateByPrimaryKeySelective(entrust);
        if(i!=1)
        {
            log.error("[委托服务] 更新委托状态失败 entrust");
            throw new EntrustException(ExceptionEnum.ENTRUST_STATUS_UPDATE_FAILED);
        }
        //改变接受的委托表中的状态
        AcceptEntrust acceptEntrust = new AcceptEntrust();
        acceptEntrust.setEntrust_id(id);
        acceptEntrust.setAccept_status(2); //改变接受人状态为 已完成
        int i1 = acceptEntrustMapper.updateByPrimaryKeySelective(acceptEntrust);
        if(i1!=1)
        {
            log.error("[委托服务] 更新委托状态失败 acceptEntrust");
            throw new EntrustException(ExceptionEnum.ENTRUST_STATUS_UPDATE_FAILED);
        }
    }
}
