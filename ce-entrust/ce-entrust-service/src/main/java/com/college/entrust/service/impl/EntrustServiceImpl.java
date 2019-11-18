package com.college.entrust.service.impl;

import com.college.auth.pojo.UserInfo;
import com.college.common.Exception.EntrustException;
import com.college.common.enums.AcceptStatusEnum;
import com.college.common.enums.ExceptionEnum;
import com.college.common.enums.ReleaseStatusEnum;
import com.college.common.enums.StatusEnum;
import com.college.common.utils.IdWorker;
import com.college.common.vo.PageResult;
import com.college.entrust.config.ImageProperties;
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
import org.omg.PortableInterceptor.ObjectReferenceFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;
import tk.mybatis.mapper.entity.Example;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@EnableConfigurationProperties(ImageProperties.class)
public class EntrustServiceImpl implements EntrustService {

    @Autowired
    private EntrustMapper entrustMapper;

    @Autowired
    private AcceptEntrustMapper acceptEntrustMapper;

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private ImageProperties imageProperties;

    @Autowired
    private AmqpTemplate amqpTemplate;

    private static final List<String> ALLOW_TYPE= Arrays.asList("image/jpeg","image/png","image/bmp");




    //分页查询所有委托
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

    //根据id查询委托
    @Override
    public Entrust queryEntrustById(Long id) {
        Entrust entrust = entrustMapper.selectByPrimaryKey(id);
        if(entrust==null)
        {
            throw new EntrustException(ExceptionEnum.ENTRUST_NOT_FOUND);
        }

        return entrust;
    }

    //接受委托
    @Transactional
    @Override
    public void acceptEntrust(Long id) {
        //1.将原先的委托的状态置为已接受
        Entrust entrust=new Entrust();
        entrust.setStatus(StatusEnum.ACCEPT.getValue());  //将委托状态置为已接受
        entrust.setId(id);
        entrustMapper.updateByPrimaryKeySelective(entrust);
        //2.将当前的用户信息与委托id存入接受委托的表中，将状态置为接受
        Entrust en = entrustMapper.selectOne(entrust);
        AcceptEntrust acceptEntrust = new AcceptEntrust();
        //拼装数据
        acceptEntrust.setAccept_status(AcceptStatusEnum.CONFIRM.getValue()); //接受状态
        acceptEntrust.setAccept_time(new Date()); //接受时间
        acceptEntrust.setEntrust_id(en.getId()); //委托id
        acceptEntrust.setRelease_uid(en.getUser_id()); //发布人id
        acceptEntrust.setRelease_status(ReleaseStatusEnum.NOT_CONFIRM.getValue()); //对方状态为 未确认
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
        Map<String,Object> paramMap=new HashMap<>();
        paramMap.put("status",1); //委托状态
        paramMap.put("user_id",en.getUser_id()); //发布人id
        paramMap.put("entrust_id",en.getId());
        //发送amqp消息通知
        amqpTemplate.convertAndSend("ce.entrust.status.exchange","entrust.status.message",paramMap);
    }

    //查询已接受的委托
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
                if (entrust.getId().equals(acceptEntrust1.getEntrust_id())) {
                    entrust.setAcceptEntrust(acceptEntrust1);
                    entrusts1.add(entrust);
                }
            }
        }
        //返回entrust集合
        return entrusts1;
    }

    //放弃委托
    @Transactional
    @Override
    public void deleteAcceptEntrust(Long id) {
        UserInfo user = UserInterceptor.getUser();
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
        entrust.setStatus(StatusEnum.DID_NOT_RECEIVE.getValue());
        int i1 = entrustMapper.updateByPrimaryKeySelective(entrust);
        if(i1!=1)
        {
            throw new EntrustException(ExceptionEnum.ENTRUST_STATUS_UPDATE_FAILED);
        }
        Entrust entrust1 = entrustMapper.selectByPrimaryKey(id);
        //拼装要发送的信息
        Map<String,Object> paramMap=new HashMap<>();
        paramMap.put("status",-1); //委托状态
        paramMap.put("user_id",entrust1.getUser_id()); //发布人id
        paramMap.put("entrust_id",entrust.getId());
        //发送amqp消息通知
        amqpTemplate.convertAndSend("ce.entrust.status.exchange","entrust.status.message",paramMap);
    }


    //根据id查询委托详情
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
           return entrust;
        }
        entrust.setAcceptEntrust(acceptEntrust);
        return entrust;
    }


    //接受委托的人确认完成委托
    @Transactional
    @Override
    public void confirmEntrust(Long id) {
        //改变接受的委托表中的状态
        AcceptEntrust acceptEntrust = new AcceptEntrust();
        acceptEntrust.setEntrust_id(id);
        acceptEntrust.setAccept_status(AcceptStatusEnum.FINISH.getValue()); //改变接受人状态为 已完成
        int i1 = acceptEntrustMapper.updateByPrimaryKeySelective(acceptEntrust);
        if(i1!=1)
        {
            log.error("[委托服务] 更新委托状态失败 acceptEntrust");
            throw new EntrustException(ExceptionEnum.ENTRUST_STATUS_UPDATE_FAILED);
        }
        Entrust entrust = entrustMapper.selectByPrimaryKey(id);
        //拼装要发送的信息
        Map<String,Object> paramMap=new HashMap<>();
        paramMap.put("status",3); //委托状态
        paramMap.put("user_id",entrust.getUser_id()); //发布人id
        paramMap.put("entrust_id",entrust.getId());
        //发送amqp消息通知
        amqpTemplate.convertAndSend("ce.entrust.status.exchange","entrust.status.message",paramMap);
    }

    //根据用户查询用户已发布委托
    @Override
    public List<Entrust> findEntrustByUser() {
        //获取用户信息
        UserInfo user = UserInterceptor.getUser();
        //根据用户id查询已发布的委托
        Entrust entrust = new Entrust();
        entrust.setUser_id(user.getId());
        List<Entrust> entrusts = entrustMapper.select(entrust);
        if(CollectionUtils.isEmpty(entrusts)){
            log.error("[委托微服务] 根据用户查找委托信息失败 用户名：{}",user.getUsername());
            throw new EntrustException(ExceptionEnum.ENTRUST_NOT_FOUND);
        }
        //查询委托的状态表
        AcceptEntrust acceptEntrust = new AcceptEntrust();
        acceptEntrust.setRelease_uid(user.getId());
        List<AcceptEntrust> acceptEntrusts = acceptEntrustMapper.select(acceptEntrust);
        if(CollectionUtils.isEmpty(acceptEntrusts))
        {
            return entrusts;
        }
        //遍历集合组装数据
        for (Entrust entrust1 : entrusts) {
            for (AcceptEntrust acceptEntrust1 : acceptEntrusts) {
                if(acceptEntrust1.getEntrust_id().equals(entrust1.getId()))
                {
                    entrust1.setAcceptEntrust(acceptEntrust1);
                }
            }
        }

        return entrusts;
    }

    //创建委托
    @Transactional
    @Override
    public void createEntrust(MultipartFile file,MultipartFile file1,MultipartFile file2, String title, String  sub_title, String  detail) {
        //1获取当前登录用户的信息
        UserInfo user = UserInterceptor.getUser();
        //2拼装数据
        Entrust entrust=new Entrust();
        entrust.setCreate_time(new Date());
        entrust.setUser_id(user.getId());
        entrust.setTitle(title);
        entrust.setSub_title(sub_title);
        entrust.setDetail(detail);
        entrust.setStatus(StatusEnum.DID_NOT_RECEIVE.getValue()); //委托状态
        try {
            //3上传图片
            //3.1判断文件类型
            if(!ALLOW_TYPE.contains(file.getContentType()))
            {
                throw new EntrustException(ExceptionEnum.INVALID_FILE_TYPE);
            }
            //3.2校验文件内容是否为图片
            BufferedImage image = ImageIO.read(file.getInputStream());
            if(image==null)
            {
                throw new EntrustException(ExceptionEnum.INVALID_FILE_TYPE);
            }
            //上传文件到本地
            File dest=new File(imageProperties.getUrl(),file.getOriginalFilename());
            file.transferTo(dest);
            File dest1=new File(imageProperties.getUrl(),file1.getOriginalFilename());
            file1.transferTo(dest1);
            File dest2=new File(imageProperties.getUrl(),file2.getOriginalFilename());
            file2.transferTo(dest2);
            //将文件路径保存到数据库
            entrust.setImage(dest.getPath()+","+dest1.getPath()+","+dest2.getPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        //4委托编号(利用雪花算法 来获取id)
        long entrust_id = idWorker.nextId();
        entrust.setId(entrust_id);
        //5将信息保存到数据库
        int insert = entrustMapper.insert(entrust);
        if(insert!=1)
        {
            throw new EntrustException(ExceptionEnum.ENTRUST_INSERT_ERROR);
        }

    }

    //修改委托 回显数据
    @Override
    public Entrust echoEntrust(Long id) {
        //获取当前登录的用户信息
        UserInfo user = UserInterceptor.getUser();
        //回显
        Entrust entrust = new Entrust();
        entrust.setId(id);
        entrust.setUser_id(user.getId());
        Entrust entrust1 = entrustMapper.selectOne(entrust);
        if(entrust1==null)
        {
            log.error("[委托微服务]，查询要修改的委托失败，用户为，{}，委托单号为，{}",user.getUsername(),id);
            throw new EntrustException(ExceptionEnum.ENTRUST_NOT_FOUND);
        }
        return entrust1;
    }

    //修改委托
    @Transactional
    @Override
    public void updateEntrust(MultipartFile file, MultipartFile file1, MultipartFile file2, String title, String sub_title, String detail, Long id) {
        //查询出原来的委托的图片路径
        Entrust entrust1 = entrustMapper.selectByPrimaryKey(id);
        deleteImage(entrust1);
        //拼装属性
        Entrust entrust = new Entrust();
        entrust.setId(id);
        entrust.setTitle(title);
        entrust.setSub_title(sub_title);
        entrust.setDetail(detail);
        try {
            //3上传图片
            //3.1判断文件类型
            if (!ALLOW_TYPE.contains(file.getContentType())) {
                throw new EntrustException(ExceptionEnum.INVALID_FILE_TYPE);
            }
            //3.2校验文件内容是否为图片
            BufferedImage image = null;
            image = ImageIO.read(file.getInputStream());
            if (image == null) {
                throw new EntrustException(ExceptionEnum.INVALID_FILE_TYPE);
            }
            //上传文件到本地
            File dest = new File(imageProperties.getUrl(), file.getOriginalFilename());
            file.transferTo(dest);
            File dest1 = new File(imageProperties.getUrl(), file1.getOriginalFilename());
            file1.transferTo(dest1);
            File dest2 = new File(imageProperties.getUrl(), file2.getOriginalFilename());
            file2.transferTo(dest2);
            //将文件路径保存到数据库
            entrust.setImage(dest.getPath() + "," + dest1.getPath() + "," + dest2.getPath());
            int i = entrustMapper.updateByPrimaryKeySelective(entrust);
            if(i!=1)
            {
                log.error("[委托微服务]，修改委托失败，委托单号为，{}",id);
                throw new EntrustException(ExceptionEnum.UPDATE_ENTRUST_ERROR);
            }
        }catch (Exception e)
        {
            log.error("[委托微服务]，修改委托失败，委托单号为，{}",id);
            throw new EntrustException(ExceptionEnum.UPDATE_ENTRUST_ERROR);
        }
    }

    //删除委托
    @Transactional
    @Override
    public void deleteEntrust(Long id) {
        //先查出该委托 获取图片路径
        Entrust entrust = entrustMapper.selectByPrimaryKey(id);
        deleteImage(entrust);
        //删除委托信息
        int i = entrustMapper.deleteByPrimaryKey(id);
        if(i!=1)
        {
            throw new EntrustException(ExceptionEnum.DELETE_ENTRUST_ERROR);
        }
    }

    //修改发布人的委托状态 为已确定
    @Transactional
    @Override
    public void releaseConfirm(Long id) {

        //修改发布人委托状态为1 （已确定）
        AcceptEntrust acceptEntrust=new AcceptEntrust();
        acceptEntrust.setEntrust_id(id);
        acceptEntrust.setRelease_status(AcceptStatusEnum.CONFIRM.getValue());
        int i = acceptEntrustMapper.updateByPrimaryKeySelective(acceptEntrust);
        if(i!=1)
        {
            throw new EntrustException(ExceptionEnum.UPDATE_ENTRUST_ERROR);
        }
        AcceptEntrust acceptEntrust1 = acceptEntrustMapper.selectOne(acceptEntrust);
        //修改委托状态为2 双方已确定
        Entrust entrust=new Entrust();
        entrust.setStatus(StatusEnum.CONFIRM.getValue());
        entrust.setId(id);
        int i1 = entrustMapper.updateByPrimaryKeySelective(entrust);
        if(i1!=1)
        {
            throw new EntrustException(ExceptionEnum.UPDATE_ENTRUST_ERROR);
        }
        //拼装要发送的信息
        Map<String,Object> paramMap=new HashMap<>();
        paramMap.put("status",2); //委托状态
        paramMap.put("user_id",acceptEntrust1.getAccept_uid()); //接受人id
        paramMap.put("entrust_id",id);
        //发送amqp消息通知
        amqpTemplate.convertAndSend("ce.entrust.status.exchange","entrust.status.message",paramMap);
    }

    //修改发布人的状态为已完成
    @Override
    public void releaseFinish(Long id) {
        //修改发布人委托状态为2 （已完成）
        AcceptEntrust acceptEntrust=new AcceptEntrust();
        acceptEntrust.setEntrust_id(id);
        acceptEntrust.setRelease_status(AcceptStatusEnum.FINISH.getValue());
        int i = acceptEntrustMapper.updateByPrimaryKeySelective(acceptEntrust);
        if(i!=1)
        {
            throw new EntrustException(ExceptionEnum.UPDATE_ENTRUST_ERROR);
        }
        //修改委托状态为2 双方已确定
        Entrust entrust=new Entrust();
        entrust.setStatus(StatusEnum.FINISH.getValue());
        entrust.setId(id);
        int i1 = entrustMapper.updateByPrimaryKeySelective(entrust);
        if(i1!=1)
        {
            throw new EntrustException(ExceptionEnum.UPDATE_ENTRUST_ERROR);
        }

        AcceptEntrust acceptEntrust1 = acceptEntrustMapper.selectOne(acceptEntrust);
        Entrust entrust1 = entrustMapper.selectByPrimaryKey(id);
        //拼装要发送的信息
        Map<String,Object> paramMap=new HashMap<>();
        paramMap.put("status",4); //委托状态
        paramMap.put("user_id",acceptEntrust1.getAccept_uid()); //接受人id
        paramMap.put("entrust_id",entrust1.getId());
        //发送amqp消息通知
        amqpTemplate.convertAndSend("ce.entrust.status.exchange","entrust.status.message",paramMap);
    }

    /**
     * 删除图片
     * @param entrust
     */
    private void deleteImage(Entrust entrust) {
        String[] split = entrust.getImage().split(",");
        for (String image : split) {
            File img=new File(image);
            if(img.exists()&&img.isFile())
            {
                if(img.delete())
                {
                    log.info("文件删除成功，文件全路径：{}",image);
                }
            }
        }
    }
}
