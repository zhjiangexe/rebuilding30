package com.rebuilding.day.service;

import com.rebuilding.day.entity.Issue;

import java.util.Map;

public interface IssueService {

    Issue findOrSave(Map<String, String> map);

    Issue findById(Long id);

    Issue save(Issue issue);
}
