package com.rebuilding.day17.repository;

import com.rebuilding.day17.entity.Issue;

public interface IssueRepository {

    Issue save(Issue issue);

    Issue findById(Long id);
}
