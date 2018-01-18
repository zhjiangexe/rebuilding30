package com.rebuilding.day.repository;

import com.rebuilding.day.entity.Issue;

public interface IssueRepository {

    Issue findById(Long id);

    Issue getBigIssue();
}
