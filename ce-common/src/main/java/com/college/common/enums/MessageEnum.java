package com.college.common.enums;

public enum MessageEnum {

    ACCEPTED_BY_OTHER("尊敬的用户你好，你的委托已被对方已接受，请您尽快确认"),
    ABANDON_ENTRUST("尊敬的用户你好，你的委托已被对方放弃，请您不要着急，会有有缘人的"),
    CONFIRM_BY_OTHER("尊敬的用户你好，你接受的委托已被确认，请您尽快完成并在页面中点击确认，我们将以通知的方式通知对方。"),
    FINISH_BY_OTHER("尊敬的用户你好，你发布的委托已被完成，请您尽快确认是否完成，并在我的委托页面进行确认。"),
    ENTRUST_OVER("尊敬的用户你好，恭喜您，本次委托圆满完成，尽管生活很重，我们依然负重前行，祝您生活愉快。"),
    ;
    private String msg;

     MessageEnum(String msg)
    {
        this.msg=msg;
    }

    public String getValue(){

     return this.msg;
     }
}
