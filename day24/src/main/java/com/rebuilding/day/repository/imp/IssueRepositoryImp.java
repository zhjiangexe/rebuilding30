package com.rebuilding.day.repository.imp;

import com.rebuilding.day.entity.Issue;
import com.rebuilding.day.repository.IssueRepository;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
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

    @Override
    public Issue getBigIssue() {
        Issue issue = new Issue();
        issue.setTitle("BIG");
        return issue;
    }
}
