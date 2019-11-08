package com.college.common.vo;

import lombok.Data;

import java.util.List;

@Data
public class PageResult<T> {
      private Long total;// 总条数
      private Long totalPage;// 总页数
      private List<T> entrusts;// 当前页数据

      public PageResult() {
      }

      public PageResult(Long total, List<T> entrusts) {
          this.total = total;
          this.entrusts = entrusts;
      }

      public PageResult(Long total, Long totalPage, List<T> entrusts) {
          this.total = total;
          this.totalPage = totalPage;
          this.entrusts = entrusts;
      }


  }