package com.rebuilding.day.service;

import com.rebuilding.day.entity.Issue;

import java.util.List;
import java.util.Map;

public interface IssueService {

    Issue queryById(Long id);

    Issue update(Issue issue);

    List<Issue> search(String keyword);
}
