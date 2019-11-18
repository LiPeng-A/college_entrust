package com.college.face.api;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

public interface FaceApi {

    //.人脸识别登录
    @PostMapping(value = "/faceLogin")
    Map<String,Object> loginFace(@RequestParam("url") String str);

    /**
     * 人脸注册，将人脸注册到人脸库中
     * @param str
     * @param id
     * @return
     */
    @PostMapping(value = "/faceRegister")
    Map<String,Object> registryFace(@RequestParam("url") String str,@RequestParam("fid") Long id);


    /**
     * 查询当前组内的用户id集合
     *
     * @return
     */
    @GetMapping("list")
    List<String> queryFaceIds();

    /**
     * 删除人脸信息
     * @param id
     * @return
     */
    @DeleteMapping("deleteFace/{id}")
    Map<String,Object> deleteFace(@PathVariable("id")Long id);


    /**
     * 更新人脸信息
     * @param str
     * @param id
     * @return
     */
    @PostMapping("revampFace")
    Map<String,Object> updateFace(@RequestParam("url")String str,
                                  @RequestParam("id")Long id
    );

}
