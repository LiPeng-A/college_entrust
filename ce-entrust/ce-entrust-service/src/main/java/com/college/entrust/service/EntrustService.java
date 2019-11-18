package com.college.entrust.service;

import com.college.common.vo.PageResult;
import com.college.entrust.pojo.Entrust;
import com.college.entrust.vo.SearchRequest;
import org.springframework.web.multipart.MultipartFile;

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

    //查询用户已发布的委托
    List<Entrust> findEntrustByUser();

    //添加委托
    void createEntrust(MultipartFile file,MultipartFile file1,MultipartFile file2, String title,String  sub_title,String  detail);

    //回显委托
    Entrust echoEntrust(Long id);

    //修改委托
    void updateEntrust(MultipartFile file, MultipartFile file1, MultipartFile file2, String title, String sub_title, String detail, Long id);

    //删除委托
    void deleteEntrust(Long id);

    //确认该委托被接收
    void releaseConfirm(Long id);

    //确认对方完成任务
    void releaseFinish(Long id);
}
