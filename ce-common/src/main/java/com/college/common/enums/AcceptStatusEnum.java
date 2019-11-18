package com.college.common.enums;

public enum AcceptStatusEnum {

    CONFIRM(1,"接受"),
    FINISH(2,"确定完成"),
    ;
    private Integer acceptStatus;
    private String desc;

    AcceptStatusEnum(Integer acceptStatus, String desc){
        this.acceptStatus=acceptStatus;
        this.desc=desc;
    }

    public Integer getValue() {
        return this.acceptStatus;
    }
}
