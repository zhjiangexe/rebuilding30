package com.rebuilding.day16.repository;

import com.rebuilding.day16.entity.Issue;

import java.util.HashMap;
import java.util.Map;

/**
 * 1. 建立一個實作IssueRepository的IssueRepositoryFake，並將他設定為final避免被繼承
 */
final public class IssueRepositoryFake implements IssueRepository {

    /**
     * 2. 加入一個private long nextId屬性，該屬性會儲存
     */
    private long nextId;

    /**
     * 3. 加入一個Map<Long, Issue>屬性，它將會用來儲存Issue物件
     */
    private Map<Long, Issue> issueMap;

    /**
     * 4. 透過建構函式初始化屬性
     */
    public IssueRepositoryFake() {
        this.nextId = 1L;
        this.issueMap = new HashMap<>();
    }

    /**
     * 5. 實作介面的方法findById(Long id)，它將會從issueMap取得物件
     */
    @Override
    public Issue findById(Long id) {
        return issueMap.get(id);
    }

    /**
     * 6. 實作介面的方法save(Issue issue)，它將會
     *      1. 分配nextId給準備儲存的Issue物件
     *      2. 將Issue放到Map
     *      3. 將nextId屬性值加1
     *      4. 回傳Issue物件
     */
    @Override
    public Issue save(Issue issue) {
        long id = nextId;
        issue.setId(id);

        issueMap.put(id, issue);
        nextId++;

        return issue;
    }
}
