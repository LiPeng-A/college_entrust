package com.college.user.client;

import com.college.face.api.FaceApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient("face-service")
public interface FaceClient extends FaceApi {


}
