package com.college.message.client;

import com.college.entrust.api.EntrustApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient("entrust-service")
public interface EntrustClient extends EntrustApi {
}
