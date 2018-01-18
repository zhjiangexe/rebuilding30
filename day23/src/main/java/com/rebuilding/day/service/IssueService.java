package com.rebuilding.day.service;

import com.rebuilding.day.entity.Issue;

import java.util.List;
import java.util.Map;

public interface IssueService {

    List<Issue> queryAll();

    Issue queryById(Long id);

    Issue create(Issue issue);

    Issue findOrSave(Map<String, String> map);
}
