package com.rebuilding.day17;

import com.rebuilding.day17.entity.Issue;
import com.rebuilding.day17.repository.IssueRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * 1. 建立一個實作IssueRepository的IssueRepositorySpy，並將他設定為final避免被繼承
 */
final public class IssueRepositorySpy implements IssueRepository {

    /**
     * 2. 加入一個int saveExecuteCount屬性，該屬性用來紀錄方法save()執行的次數
     */
    private int saveExecuteCount;

    /**
     * 3. 提供一個可以取得記錄執行次數的方法
     */
    public int getSaveExecuteCount() {
        return saveExecuteCount;
    }

    /**
     * 4. 加入一個屬性List<Issue> issueList，該List用來記錄帶入save()方法參數的Issue物件
     */
    private List<Issue> issueList;

    /**
     * 5. 提供一個取得列表的方法
     */
    public List<Issue> getSavedIssueList() {
        return issueList;
    }

    /**
     * 6. 透過建構函式初始化用來記錄的屬性
     */
    public IssueRepositorySpy() {
        saveExecuteCount = 0;
        issueList = new ArrayList<>();
    }

    /**
     * 7. 實作方法save，並編寫通過此方法欲紀錄的資料
     */
    @Override
    public Issue save(Issue issue) {
        saveExecuteCount++;
        issueList.add(issue);
        return issue;
    }

    @Override
    public Issue findById(Long id) {
        return null;
    }
}
