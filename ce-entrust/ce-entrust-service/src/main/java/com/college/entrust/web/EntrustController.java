package com.college.entrust.web;

import com.college.common.vo.PageResult;
import com.college.entrust.pojo.Entrust;
import com.college.entrust.service.EntrustService;
import com.college.entrust.vo.SearchRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
public class EntrustController {

    @Autowired
    private EntrustService entrustService;

    /**
     * 分页查询所有的委托信息
     *
     * @return
     */
    @PostMapping("/list")
    public ResponseEntity<PageResult<Entrust>> queryEntrustList(@RequestBody SearchRequest searchRequest) {

        return ResponseEntity.ok(entrustService.queryEntrustList(searchRequest));
    }

    /**
     * 根据id查询当前委托详情
     *
     * @param id
     * @return
     */
    @GetMapping("detail/{id}")
    public ResponseEntity<Entrust> queryEntrustById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(entrustService.queryEntrustById(id));
    }

    /**
     * 接受委托
     *
     * @param id
     * @return
     */
    @PutMapping("access/{id}")
    public ResponseEntity<Void> acceptEntrust(@PathVariable("id") Long id) {

        entrustService.acceptEntrust(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * 根据用户id查询接受的所有委托
     *
     * @return
     */
    @GetMapping("accept/list")
    public ResponseEntity<List<Entrust>> queryAcceptEntrust() {

        return ResponseEntity.ok(entrustService.queryAcceptEntrust());
    }

    /**
     * 根据id删除要放弃的委托
     *
     * @param id
     * @return
     */
    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteAcceptEntrust(@PathVariable("id") Long id) {

        entrustService.deleteAcceptEntrust(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * 查询接受的委托的详情
     *
     * @param id
     * @return
     */
    @GetMapping("accept-detail/{e_id}")
    public ResponseEntity<Entrust> queryEntrustDetailById(@PathVariable("e_id") Long id) {

        return ResponseEntity.ok(entrustService.queryEntrustDetailById(id));
    }

    /**
     * 确定完成委托
     *
     * @param id
     * @return
     */
    @PutMapping("confirm/{id}")
    public ResponseEntity<Void> confirmEntrust(@PathVariable("id") Long id) {
        entrustService.confirmEntrust(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * 查询用户已发布的委托
     *
     * @return
     */
    @GetMapping("release")
    public ResponseEntity<List<Entrust>> findEntrustByUser() {
        return ResponseEntity.ok(entrustService.findEntrustByUser());
    }

    /**
     * 添加新的委托
     *
     * @param
     * @return
     */
    @PostMapping("newEntrust")
    public ResponseEntity<Void> createEntrust(@RequestParam("file") MultipartFile file,
                                              @RequestParam("file1") MultipartFile file1,
                                              @RequestParam("file2") MultipartFile file2,
                                              @RequestParam("title") String title,
                                              @RequestParam("sub_title") String sub_title,
                                              @RequestParam("detail") String detail) {
        entrustService.createEntrust(file,file1 ,file2,title, sub_title, detail);
        return ResponseEntity.status(HttpStatus.CREATED).build();

    }

    /**
     * 回显要修改的委托信息
     * @param id
     * @return
     */
    @GetMapping("echo/{id}")
    public ResponseEntity<Entrust> echoEntrust(@PathVariable("id")Long id){
        return ResponseEntity.ok(entrustService.echoEntrust(id));
    }

    /**
     * 修改当前委托
     * @param file
     * @param file1
     * @param file2
     * @param title
     * @param sub_title
     * @param detail
     * @param id
     * @return
     */
    @PostMapping("new")
    public ResponseEntity<Void> updateEntrust(@RequestParam("file") MultipartFile file,
                                              @RequestParam("file1") MultipartFile file1,
                                              @RequestParam("file2") MultipartFile file2,
                                              @RequestParam("title") String title,
                                              @RequestParam("sub_title") String sub_title,
                                              @RequestParam("detail") String detail,
                                              @RequestParam("id")Long id
                                              ) {
        entrustService.updateEntrust(file,file1 ,file2,title, sub_title, detail,id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();

    }

    /**
     * 根据id删除委托
     * @param id
     * @return
     */
    @DeleteMapping("/data/{id}")
    public ResponseEntity<Void> deleteEntrust(@PathVariable("id")Long id)
    {
        entrustService.deleteEntrust(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();

    }

    /**
     * 确认被该用户接受委托
     * @param id
     * @return
     */
    @PutMapping("releaseConfirm/{id}")
    public ResponseEntity<Void> releaseConfirm(@PathVariable("id")Long id){
        entrustService.releaseConfirm(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * 确认对方完成任务
     * @param id
     * @return
     */
    @PutMapping("releaseFinish/{id}")
    public ResponseEntity<Void> releaseFinish(@PathVariable("id")Long id)
    {
        entrustService.releaseFinish(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
