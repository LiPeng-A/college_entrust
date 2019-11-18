package com.college.common.enums;

public enum StatusEnum {

    DID_NOT_RECEIVE(0,"未接受"),
    ACCEPT(1,"已接受"),
    CONFIRM(2,"已确定"),
    FINISH(3,"已完成")
    ;
    private Integer entrustStatus;
    private String desc;

    StatusEnum(Integer entrustStatus,String desc){
        this.entrustStatus=entrustStatus;
        this.desc=desc;
    }

    public Integer getValue() {
        return this.entrustStatus;
    }
}
