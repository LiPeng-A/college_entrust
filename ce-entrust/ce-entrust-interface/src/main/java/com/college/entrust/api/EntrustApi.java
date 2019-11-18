package com.college.entrust.api;

import com.college.entrust.pojo.Entrust;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

public interface EntrustApi {
    /**
     * 查询接受的委托的详情
     *
     * @param id
     * @return
     */
    @GetMapping("accept-detail/{e_id}")
    Entrust queryEntrustDetailById(@PathVariable("e_id") Long id);
}
