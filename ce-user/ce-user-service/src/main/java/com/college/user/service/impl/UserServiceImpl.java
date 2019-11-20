package com.college.user.service.impl;

import com.college.auth.pojo.UserInfo;
import com.college.auth.utils.JwtUtils;
import com.college.common.Exception.EntrustException;
import com.college.common.enums.ExceptionEnum;
import com.college.common.utils.NumberUtils;
import com.college.user.client.FaceClient;
import com.college.user.config.JwtProperties;
import com.college.user.config.SaltProperties;
import com.college.user.mapper.UserMapper;
import com.college.user.pojo.User;
import com.college.user.service.UserService;
import com.college.user.utils.CodecUtils;
import com.college.user.vo.PasswordRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@EnableConfigurationProperties({SaltProperties.class,JwtProperties.class})
public class UserServiceImpl implements UserService {

    private static final String KEY_PREFIX="user:verify:phone";

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private AmqpTemplate amqpTemplate;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private SaltProperties saltProperties;

    @Autowired
    private FaceClient faceClient;

    @Autowired
    private JwtProperties jwtProperties;


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

    //注册用户
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
        String encodePassword = CodecUtils.passwordBcryptEncode(saltProperties.getSalt(), user.getPassword());
        //将用户信息保存到数据库中
        user.setCreated(new Date());
        //将加密过的密码存入数据库
        user.setPassword(encodePassword);
        //用毫秒值做id
        user.setId(System.currentTimeMillis());
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
        if (!CodecUtils.passwordBcryptDecode(saltProperties.getSalt()+password,user1.getPassword())) {
            throw new EntrustException(ExceptionEnum.INVALID_USERNAME_AND_PASSWORD);
        }
        return user1;
    }

    @Override
    public User queryUserById(Long user_id) {
        User user = userMapper.selectByPrimaryKey(user_id);
        if(user==null)
        {
            throw new EntrustException(ExceptionEnum.USER_NOT_FOUND);
        }
        return user;
    }

    @Override
    public void updateUser(User user) {
        int i = userMapper.updateByPrimaryKeySelective(user);
        if(i!=1)
        {
            throw new EntrustException(ExceptionEnum.UPDATE_USER_ERROR);
        }
    }

    @Override
    public void alertPassword(PasswordRequest request) {
        //先查出该用户信息
        User user = userMapper.selectByPrimaryKey(request.getId());
        //判断输入的原密码是否正确
        if(!CodecUtils.passwordBcryptDecode(saltProperties.getSalt()+request.getPassword(),user.getPassword()))
        {
            throw new EntrustException(ExceptionEnum.PASSWORD_INVALID);
        }

        //判断两次密码是否正确
        if(!StringUtils.equals(request.getNewPassword(),request.getNewPassword()))
        {
            throw new EntrustException(ExceptionEnum.CONFIRM_PASSWORD_ERROR);
        }

        //将新密码加密
        String caChePassword = CodecUtils.passwordBcryptEncode(saltProperties.getSalt(), request.getNewPassword());
        //将新密码存入数据库
        User user1=new User();
        user1.setId(request.getId());
        user1.setPassword(caChePassword);
        userMapper.updateByPrimaryKeySelective(user1);
    }

    @Transactional
    @Override
    public Map<String, Object> faceRegister(String str, String fid) {
        String id=fid;
        //判断id是否已经存在
        List<String> strings = faceClient.queryFaceIds();
        for (String string : strings) {
            if(StringUtils.equals(fid,string))
            {
                throw new EntrustException(ExceptionEnum.FACE_INVALID);
            }
        }
        //注册人脸到人脸库
        Map<String, Object> map = faceClient.registryFace(str, Long.valueOf(fid));
        if(CollectionUtils.isEmpty(map))
        {
            throw new EntrustException(ExceptionEnum.FACE_REGISTER_ERROR);
        }
        //修改用户在数据库的信息
        User user=new User();
        user.setIs_face(1);
        user.setId(Long.valueOf(fid));
        userMapper.updateByPrimaryKeySelective(user);

        return map;
    }

    //删除人脸信息
    @Override
    public void delFace(Long id) {
        Map<String, Object> map = faceClient.deleteFace(id);
        String error_msg = (String) map.get("error_msg");
        if (!StringUtils.equals(error_msg,"SUCCESS")) {
            throw new EntrustException(ExceptionEnum.DELETE_FACE_ERROR);
        }
        User user=new User();
        user.setId(id);
        user.setIs_face(0);
        userMapper.updateByPrimaryKeySelective(user);
    }

    @Override
    public Map<String, Object> updateFace(String str, Long id) {
        Map<String, Object> map = faceClient.updateFace(str, id);
        if(CollectionUtils.isEmpty(map))
        {
            throw new EntrustException(ExceptionEnum.FACE_INVALID);
        }

        return map;
    }

    @Override
    public String  faceLogin(String str) {
        //人脸验证
        Map<String, Object> map = faceClient.loginFace(str);
        if(CollectionUtils.isEmpty(map))
        {
            throw new EntrustException(ExceptionEnum.FACE_INVALID);
        }
        //判断人脸是不是正确
        Map<String,Object> result = (Map<String, Object>) map.get("result");
        if(CollectionUtils.isEmpty(result))
        {
            throw new EntrustException(ExceptionEnum.FACE_INVALID);
        }
        List<Map<String,String>> user_list = (List<Map<String, String>>) result.get("user_list");
        Map<String, String> map1 = user_list.get(0);
        String user_id = map1.get("user_id");
        User user = userMapper.selectByPrimaryKey(Long.valueOf(user_id));
        if(user==null)
        {
            throw new EntrustException(ExceptionEnum.USER_NOT_FOUND);
        }
        try {
            String token = JwtUtils.generateToken(new UserInfo(Long.valueOf(user_id), user.getUsername()),
                    jwtProperties.getPrivateKey(), jwtProperties.getExpire());
            return token;
        } catch (Exception e) {
            throw new EntrustException(ExceptionEnum.INVALID_USERNAME_AND_PASSWORD);
        }

    }

    @Override
    public Boolean queryPassword(Long id, String password) {
        User user = userMapper.selectByPrimaryKey(id);
        if(user==null)
        {
            throw new EntrustException(ExceptionEnum.USER_NOT_FOUND);
        }
        Boolean flag = CodecUtils.passwordBcryptDecode(saltProperties.getSalt() + password, user.getPassword());


        return flag;
    }
}
