package com.college.face.web;

import com.baidu.aip.face.AipFace;
import com.baidu.aip.util.Base64Util;
import com.college.common.utils.JsonUtils;
import com.college.face.config.FaceProperties;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@EnableConfigurationProperties(FaceProperties.class)
@Slf4j
public class FaceController {

    @Autowired
    private FaceProperties prop;

    //005.人脸识别登录
    @PostMapping(value = "/faceLogin")
    public ResponseEntity<Map<String, Object>> loginFace(@RequestParam("url") String str) {
        //使用axios提交base64字符串，需要经过去头转码
        String img_data = str.substring(22, str.length());
        //配置AipFace参数
        AipFace client = new AipFace(prop.getApp_id(), prop.getApi_key(), prop.getSecret_key());
        //传入可选参数调用接口
        HashMap<String, String> options = new HashMap<String, String>();
        options.put("max_face_num", "3");//最多处理人脸的数目 默认值为1(仅检测图片中面积最大的那个人脸) 最大值10
        options.put("match_threshold", "80");  //阀值信息 推荐80
        options.put("quality_control", "HIGH"); //图片质量要求
        String image = str; //获取图片的信息
        String imageType = "BASE64";
        String groupId = prop.getGroup();
        //人脸注册
        JSONObject res = client.search(image, imageType, groupId, options);
        //json转map
        Map<String, Object> map = JsonUtils.parseMap(res.toString(), String.class, Object.class);
        System.out.println(res.toString(2));
        return ResponseEntity.ok(map);
    }


    /**
     * 人脸注册，将人脸注册到人脸库中
     *
     * @param str
     * @param id
     * @return
     */
    @PostMapping(value = "/faceRegister")
    public ResponseEntity<Map<String, Object>> registryFace(@RequestParam("url") String str, @RequestParam("fid") Long id) {
        //使用axios提交base64字符串，需要经过去头转码
        String img_data = str.substring(22, str.length());
        //配置AipFace参数
        AipFace client = new AipFace(prop.getApp_id(), prop.getApi_key(), prop.getSecret_key());
        //传入可选参数调用接口
        HashMap<String, String> options = new HashMap<String, String>();
        options.put("liveness_control", "NORMAL");
        String image = str;
        String imageType = "BASE64";
        String groupId = prop.getGroup();
        String fid = id.toString();
        //人脸注册
        JSONObject res = client.addUser(image, imageType, groupId, fid, options);
        //json转map
        Map<String, Object> map = JsonUtils.parseMap(res.toString(), String.class, Object.class);
        System.out.println(res.toString(2));
        return ResponseEntity.ok(map);
    }

    /**
     * 查询当前组内的用户id集合
     *
     * @return
     */
    @GetMapping("list")
    public ResponseEntity<List<String>> queryFaceIds() {
        //配置AipFace参数
        AipFace client = new AipFace(prop.getApp_id(), prop.getApi_key(), prop.getSecret_key());
        // 传入可选参数调用接口
        HashMap<String, String> options = new HashMap<String, String>();
        String groupId = prop.getGroup();
        //获取用户列表
        JSONObject res = client.getGroupUsers(groupId, options);
        System.out.println(res.toString(2));
        //json转map
        Map<String, Object> map = JsonUtils.parseMap(res.toString(), String.class, Object.class);
        Map<String,List<String>> result = (Map<String, List<String>>) map.get("result");
        List<String> user_id_list1 = result.get("user_id_list");
        return ResponseEntity.ok(user_id_list1);
    }

    /**
     * 删除人脸信息
     * @param id
     * @return
     */
    @DeleteMapping("deleteFace/{id}")
    public ResponseEntity<Map<String,Object>> deleteFace(@PathVariable("id")Long id){
        //配置AipFace参数
        AipFace client = new AipFace(prop.getApp_id(), prop.getApi_key(), prop.getSecret_key());
        // 传入可选参数调用接口
        HashMap<String, String> options = new HashMap<String, String>();
        String userId = id.toString();
        String groupId = prop.getGroup();
        //查询用户信息 获取face_token (上传人脸图片的唯一标识)
        JSONObject res =  client.faceGetlist(userId, groupId, options);
        System.out.println(res.toString(2));
        //json转map
        Map<String,Object> map = JsonUtils.parseMap(res.toString(), String.class, Object.class);
        //获取result
        Map<String,Object> result = (Map<String, Object>) map.get("result");
        List<Map<String,Object>> face_list = (List<Map<String, Object>>) result.get("face_list");
        Map<String, Object> map1 = face_list.get(0);
        String face_token = (String) map1.get("face_token");
        System.out.println(face_token);
        // 人脸删除
        JSONObject delRes = client.faceDelete(userId, groupId, face_token, options);
        //json转map
        Map<String, Object> mapRes = JsonUtils.parseMap(delRes.toString(), String.class, Object.class);
        return ResponseEntity.ok(mapRes);
    }

    /**
     * 更新人脸信息
     * @param str
     * @param id
     * @return
     */
    @PostMapping("revampFace")
    public ResponseEntity<Map<String,Object>> updateFace(@RequestParam("url")String str,
                                                         @RequestParam("id")Long id
                                                         ){
        //配置AipFace参数
        AipFace client = new AipFace(prop.getApp_id(), prop.getApi_key(), prop.getSecret_key());
        // 传入可选参数调用接口
        HashMap<String, String> options = new HashMap<String, String>();
        options.put("quality_control", "HIGH");
        options.put("liveness_control", "LOW");

        String image = str;
        String imageType = "BASE64";
        String groupId = prop.getGroup();
        String userId = id.toString();
        // 人脸更新
        JSONObject res = client.updateUser(image, imageType, groupId, userId, options);
        log.info("人脸识别微服务："+res.toString(2));
        //json转map
        Map<String,Object> map = JsonUtils.parseMap(res.toString(), String.class, Object.class);
        return ResponseEntity.ok(map);
    }

}
