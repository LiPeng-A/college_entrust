package com.college.entrust.web;

import com.college.entrust.pojo.Entrust;
import com.college.entrust.service.EntrustService;
import com.college.entrust.vo.EntrustDetail;
import com.college.entrust.vo.SearchRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class EntrustController {

    @Autowired
    private EntrustService entrustService;

    /**
     * 分页查询所有的委托信息
     * @return
     */
    @PostMapping("/list")
    public ResponseEntity<EntrustDetail> queryEntrustList(@RequestBody SearchRequest searchRequest){

        return ResponseEntity.ok(entrustService.queryEntrustList(searchRequest));
    }

}
