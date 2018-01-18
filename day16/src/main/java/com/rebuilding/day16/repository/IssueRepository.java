package com.rebuilding.day16.repository;

import com.rebuilding.day16.entity.Issue;

public interface IssueRepository {
    /**
     * 透過參數ID找到指定的Issue
     */
    Issue findById(Long id);

    /**
     * 傳入任務
     */
    Issue save(Issue issue);
}
