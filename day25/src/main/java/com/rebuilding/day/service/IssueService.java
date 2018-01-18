package com.rebuilding.day.service;

import com.rebuilding.day.entity.Issue;

import java.util.List;

public interface IssueService {

    List<Issue> queryAll();

    Issue queryById(Long id);

    Issue getBigIssue();
}
