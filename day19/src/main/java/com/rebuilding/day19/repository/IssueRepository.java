package com.rebuilding.day19.repository;

import com.rebuilding.day19.entity.Issue;

public interface IssueRepository {

    Issue save(Issue issue);

    Issue findById(Long id);
}
