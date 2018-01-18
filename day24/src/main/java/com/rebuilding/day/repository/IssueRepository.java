package com.rebuilding.day.repository;

import com.rebuilding.day.entity.Issue;

public interface IssueRepository {

    Issue save(Issue issue);

    Issue findById(Long id);

    Issue getBigIssue();
}
