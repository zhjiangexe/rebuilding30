package com.rebuilding.day14.repository;

import com.rebuilding.day14.entity.Issue;

public interface IssueRepository {
    /**
     * 回傳從資料庫中找到的Issue數量
     */
    long count();

    /**
     * 刪除參數中的Issue，如果資料庫中找不到則發生NotFoundException
     */
    void delete(Issue issue);

    /**
     * 透過參數ID找到指定的Issue
     */
    Issue findById(Long id);

    /**
     * 傳入任務
     */
    Issue save(Issue issue);

    Issue find(Issue issue);
}
