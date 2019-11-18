package com.college.common.enums;

public enum ReleaseStatusEnum {

    NOT_CONFIRM(0,"未确定"),
    ACCEPT(1,"已确定"),
    CONFIRM(2,"确定完成"),
    ;
    private Integer releaseStatus;
    private String desc;

    ReleaseStatusEnum(Integer releaseStatus, String desc){
        this.releaseStatus=releaseStatus;
        this.desc=desc;
    }

    public Integer getValue() {
        return this.releaseStatus;
    }
}
