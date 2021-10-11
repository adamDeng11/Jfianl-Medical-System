package com.maven.appointment;

/**
 * 某医生某时间段预约
 */
public class TimeListBean {
    /*存储到数据库中该事件段的标识*/
    private Integer timeValue;
    /*显示给病人的时间段*/
    private String timeText;
    /*该时间段可预约的总数*/
    private Integer totalNum;
    /*该时间段已经预约的病人数*/
    private Integer reservedNum;
    /*改时间段还能预约的病人数*/
    private Integer remainingNum;

    public TimeListBean(Integer timeValue, String timeText, Integer totalNum, Integer reservedNum, Integer remainingNum) {
        this.timeValue = timeValue == null ? 0 : timeValue;
        this.timeText = timeText;
        this.totalNum = totalNum == null ? 0 : totalNum;
        this.reservedNum = reservedNum == null ? 0 : reservedNum;
        this.remainingNum = remainingNum == null ? 0 : remainingNum;
    }

    public Integer getTimeValue() {
        return timeValue;
    }

    public void setTimeValue(Integer timeValue) {
        this.timeValue = timeValue;
    }

    public String getTimeText() {
        return timeText;
    }

    public void setTimeText(String timeText) {
        this.timeText = timeText;
    }

    public Integer getTotalNum() {
        return totalNum;
    }

    public void setTotalNum(Integer totalNum) {
        this.totalNum = totalNum;
    }

    public Integer getReservedNum() {
        return reservedNum;
    }

    public void setReservedNum(Integer reservedNum) {
        this.reservedNum = reservedNum;
    }

    public Integer getRemainingNum() {
        return remainingNum;
    }

    public void setRemainingNum(Integer remainingNum) {
        this.remainingNum = remainingNum;
    }
}