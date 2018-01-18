package com.rebuilding.day.entity;

import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
public class Issue {
    @Id
    private Long id; // issue單號

    @NotBlank
    @Column(name = "title", nullable = false, length = 128)
    private String title; // 標題

    @Column(name = "desc", length = 1024)
    private String desc; // 內容描述

    @Column(name = "creator_num")
    private Long creatorNum; // 建立人

    @Column(name = "assignee_num")
    private Long assigneeNum; // 負責人

    @Column(name = "closer_num")
    private Long closerNum; // 結案人

    @Column(name = "state")
    @Enumerated(EnumType.STRING)
    private State state; // 狀態TODO, IN_PROGRESS, DONE

    @Column(name = "result")
    @Enumerated(EnumType.STRING)
    private Result result; // 結案形式RESOLVED, REJECT

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Long getCreatorNum() {
        return creatorNum;
    }

    public void setCreatorNum(Long creatorNum) {
        this.creatorNum = creatorNum;
    }

    public Long getAssigneeNum() {
        return assigneeNum;
    }

    public void setAssigneeNum(Long assigneeNum) {
        this.assigneeNum = assigneeNum;
    }

    public Long getCloserNum() {
        return closerNum;
    }

    public void setCloserNum(Long closerNum) {
        this.closerNum = closerNum;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }
}
