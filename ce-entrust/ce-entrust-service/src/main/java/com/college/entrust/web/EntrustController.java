package com.college.entrust.web;

import com.college.common.vo.PageResult;
import com.college.entrust.pojo.AcceptEntrust;
import com.college.entrust.pojo.Entrust;
import com.college.entrust.service.EntrustService;
import com.college.entrust.vo.SearchRequest;
import com.netflix.ribbon.proxy.annotation.Http;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

    /**
     * 接受委托
     * @param id
     * @return
     */
    @PutMapping("access/{id}")
    public ResponseEntity<Void> acceptEntrust(@PathVariable("id")Long id){

        entrustService.acceptEntrust(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     *根据用户id查询接受的所有委托
     * @return
     */
    @GetMapping("accept/list")
    public ResponseEntity<List<Entrust>> queryAcceptEntrust(){

        return ResponseEntity.ok(entrustService.queryAcceptEntrust());
    }

    /**
     * 根据id删除要放弃的委托
     * @param id
     * @return
     */
    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteAcceptEntrust(@PathVariable("id")Long id){

        entrustService.deleteAcceptEntrust(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * 查询接受的委托的详情
     * @param id
     * @return
     */
    @GetMapping("accept-detail/{e_id}")
    public ResponseEntity<Entrust> queryEntrustDetailById(@PathVariable("e_id")Long id){

        return ResponseEntity.ok(entrustService.queryEntrustDetailById(id));
    }

    /**
     * 确定完成委托
     * @param id
     * @return
     */
    @PutMapping("confirm/{id}")
    public ResponseEntity<Void> confirmEntrust(@PathVariable("id")Long id)
    {
        entrustService.confirmEntrust(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
