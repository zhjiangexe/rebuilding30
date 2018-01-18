package com.rebuilding.day13.service.imp;

import com.rebuilding.day13.entity.Issue;
import com.rebuilding.day13.repository.IssueRepository;

public class IssueServiceImp {

    private IssueRepository repository;

    public Issue find(Issue issue) {
        return repository.find(issue);
    }

    /**
     * IssueRepository的注入方法
     */
    public void setRepository(IssueRepository repository) {
        this.repository = repository;
    }
}
