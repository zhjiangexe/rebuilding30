package com.rebuilding.day19.repository.imp;

import com.rebuilding.day19.entity.Issue;
import com.rebuilding.day19.repository.IssueRepository;

import java.util.HashMap;
import java.util.Map;

public class IssueRepositoryImp implements IssueRepository {
    private Map<Long, Issue> database = new HashMap<>();

    @Override
    public Issue save(Issue issue) {
        Long dataCount = database.size() + 1L;
        issue.setId(dataCount);
        database.put(dataCount, issue);
        return issue;
    }

    @Override
    public Issue findById(Long id) {
        return database.get(id);
    }
}
