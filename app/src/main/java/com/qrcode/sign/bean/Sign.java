package com.qrcode.sign.bean;
/***
 * 封装的签到对象
 *
 * */
public class Sign {
    String type;//维保任务 0，维修任务 1
    Task task;//任务内容
    String state;//签到状态  待签到 0，签到成功 1，签到失败 2 ，签到超时 3

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
