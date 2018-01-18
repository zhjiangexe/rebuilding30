package com.rebuilding.day13.repository;

import com.rebuilding.day13.entity.Issue;

/**
 * 1. 建立一個實作IssueRepository的IssueRepositoryStub，並將其設定為final避免被繼承
 */
final public class IssueRepositoryStub implements IssueRepository {

    /**
     * 2. 加入一個類別屬性private final Issue
     */
    private final Issue issue;

    /**
     * 3. 加入一個可以注入Issue物件的建構函式
     */
    IssueRepositoryStub(Issue issue) {
        this.issue = issue;
    }

    /**
     * 4. 設定find方法會回傳的找到的Issue物件，帶有title及desc
     */
    @Override
    public Issue find(Issue issue) {
        issue.setTitle(this.issue.getTitle());
        issue.setDesc(this.issue.getDesc());
        return issue;
    }
}
