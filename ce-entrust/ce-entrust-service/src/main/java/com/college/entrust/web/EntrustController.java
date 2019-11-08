package com.college.entrust.web;

import com.college.common.vo.PageResult;
import com.college.entrust.pojo.Entrust;
import com.college.entrust.service.EntrustService;
import com.college.entrust.vo.SearchRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class EntrustController {

    @Autowired
    private EntrustService entrustService;

    /**
     * 分页查询所有的委托信息
     * @return
     */
    @PostMapping("/list")
    public ResponseEntity<PageResult<Entrust>> queryEntrustList(@RequestBody SearchRequest searchRequest){

        return ResponseEntity.ok(entrustService.queryEntrustList(searchRequest));
    }

    /**
     * 根据id查询当前委托详情
     * @param id
     * @return
     */
    @GetMapping("detail/{id}")
    public ResponseEntity<Entrust> queryEntrustById(@PathVariable("id")Long id){
        return ResponseEntity.ok(entrustService.queryEntrustById(id));
    }

}
