package com.college.user.service.impl;

import com.college.common.Exception.EntrustException;
import com.college.common.enums.ExceptionEnum;
import com.college.common.utils.NumberUtils;
import com.college.user.mapper.UserMapper;
import com.college.user.pojo.User;
import com.college.user.service.UserService;
import com.college.user.utils.CodecUtils;
import org.apache.commons.lang3.StringUtils;
import org.omg.CORBA.TIMEOUT;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Valid;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl implements UserService {

    private static final String KEY_PREFIX="user:verify:phone";

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private AmqpTemplate amqpTemplate;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public Boolean checkData(String data, Integer type) {
        User user = new User();
        switch (type){
            case 1:user.setUsername(data);
            break;
            case 2:user.setPhone(data);
            break;
            default://不是这两种类型
                throw new EntrustException(ExceptionEnum.INVALID_USERNAME_TYPE);
        }
        //如果返回的数量为1 则可以使用用户名
        return userMapper.selectCount(user)==0;
    }

    @Override
    public void sendCode(String phone) {
        String key=KEY_PREFIX+phone;
        //生成验证码
        String code = NumberUtils.generateCode(6);
        HashMap<String,String> msg=new HashMap<>();
        msg.put("phone",phone);
        msg.put("code",code);
        //发送验证码
        amqpTemplate.convertAndSend("ce.sms.exchange","sms.verify.code",msg);

        //将验证码保存到redis中,只有5分钟
        redisTemplate.opsForValue().set(key,code,5,TimeUnit.MINUTES);
    }

    @Transactional //添加事务
    @Override
    public void register(User user, String code) {
        //从redis中取出验证码
        String key=KEY_PREFIX+user.getPhone();
        String cacheCode = (String) redisTemplate.opsForValue().get(key);
        //判断验证码是否正确
        if(!StringUtils.equals(code,cacheCode)){
            //验证码不正确
            throw new EntrustException(ExceptionEnum.INVALID_CODE);
        }
        //对密码进行加密
        String encodePassword = CodecUtils.passwordBcryptEncode(user.getUsername(), user.getPassword());
        //将用户信息保存到数据库中
        user.setCreated(new Date());
        //将加密过的密码存入数据库
        user.setPassword(encodePassword);
        int i = userMapper.insertSelective(user);
        if(i!=1)
        {
            throw new EntrustException(ExceptionEnum.REGISTER_ERROR);
        }
    }

    @Override
    public User login(String username, String password) {
        User user = new User();
        user.setUsername(username);
        User user1 = userMapper.selectOne(user);
        //校验是否存在
        if(user1==null)
        {
            //返回异常信息
            throw new EntrustException(ExceptionEnum.INVALID_USERNAME_AND_PASSWORD);
        }
        //校验密码是否正确
        if (!CodecUtils.passwordBcryptDecode(username+password,user1.getPassword())) {
            throw new EntrustException(ExceptionEnum.INVALID_USERNAME_AND_PASSWORD);
        }
        return user1;
    }
}
